package com.intuit.orderManagementSystem.order.management.system.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.orderManagementSystem.order.management.system.entity.Order;
import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import com.intuit.orderManagementSystem.order.management.system.entity.User;
import com.intuit.orderManagementSystem.order.management.system.enums.OrderOperation;
import com.intuit.orderManagementSystem.order.management.system.enums.OrderStatus;
import com.intuit.orderManagementSystem.order.management.system.exception.ForbiddenException;
import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.repository.OrderRepository;
import com.intuit.orderManagementSystem.order.management.system.service.serviceInterface.OrderService;
import com.intuit.orderManagementSystem.order.management.system.service.serviceInterface.ProductService;
import com.intuit.orderManagementSystem.order.management.system.service.serviceInterface.UserService;
import com.intuit.orderManagementSystem.order.management.system.utils.kafka.KafkaProducer;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl.MapOrderRequestToOrder;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl.MapOrderToOrderResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl.MapOrderToOrderSubmitResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Request.OrderRequest;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderDeleteResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderSubmitResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.PaymentStatus;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    MapOrderRequestToOrder mapOrderRequestToOrder;

    @Autowired
    MapOrderToOrderResponse mapOrderToOrderResponse;

    @Autowired
    MapOrderToOrderSubmitResponse mapOrderToOrderSubmitResponse;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public OrderResponse saveOrder(String userName, OrderRequest orderRequest) throws IdNotFoundException{
        User user = validatedUser(userName, null);

        Product product = productService.getProductPrice(orderRequest.getProductId());
        Order order = mapOrderRequestToOrder.RequestToEntity(orderRequest);
        log.info("Product and order {} {}", product, order);

        // Set user and product inside the order
        order.setUser(user);
        order.setProduct(product);
        log.info("Order : {} {} {}", order.toString(), order.getUser(), order.getProduct());

        // Checks if product exist, and is in stock
        checkIfProductExistsAndIsInStock(order);

        // Save the entity
        Order savedOrder = orderRepository.save(order);
        log.info("Saved the order {} ", savedOrder.toString());

        return mapOrderToOrderResponse.MapToResponse(savedOrder);
    }

    @Override
    public OrderResponse editOrder(String userName, OrderRequest orderRequest, Long orderId) {
        // Validate the request
        Order validatedOrder = validateOrderRequest(orderId, OrderOperation.EDIT);

        // Validate the user
        User user = validatedUser(userName, validatedOrder);

        // Set new values from the request to the order resp
        Order updatedOrder = mapOrderRequestToOrder.RequestToEntity(orderRequest);
        updatedOrder.setOrder_id(orderId);
        updatedOrder.setProduct(validatedOrder.getProduct());
        updatedOrder.setUser(validatedOrder.getUser());

        // Checks if product exist, and is in stock
        Product product = checkIfProductExistsAndIsInStock(updatedOrder);

        // Save the order
        Order savedOrder = orderRepository.save(updatedOrder);
        log.info("Edited the order {} ", savedOrder.toString());

        return mapOrderToOrderResponse.MapToResponse(savedOrder);
    }


    @Override
    public OrderDeleteResponse deleteOrder(String userName, Long orderId) {
        // Validate orderId
        Order validatedOrder  = validateOrderRequest(orderId, OrderOperation.DELETE);
        log.info("Delete order - Order found  {}", orderId);

        // Validate the user
        User user = validatedUser(userName, validatedOrder);
        log.info("Delete order - User Validated  {}", orderId);

        // Set new value to the order
        validatedOrder.setType(OrderStatus.CANCELLED);

        // Save the order
        orderRepository.save(validatedOrder);
        log.info("Delete order - Order cancelled  {}", orderId);

        return new OrderDeleteResponse("Deleted Successfully");
    }

    @Override
    @Transactional
    public OrderSubmitResponse submitOrder(String userName, Long orderId) {
        try{
            // Validate the Order request.
            Order order = validateOrderRequest(orderId, OrderOperation.SUBMIT);
            log.info("Order validated {} ", order.toString());

            // Validate the user
            User user = validatedUser(userName, order);

            // Decrease the product quantity of the ordered product.
            productService.updateQuantity(order.getProduct().getProductId(), order.getQuantity());

            // Once updating the product is successful, Update the order status to "PROCESSING".
            order.setType(OrderStatus.PAYMENT_IN_PROGRESS);
            Order orderResult = orderRepository.save(order);
            log.info("Updated order status {} ", orderResult.toString());

            // Initiate payment.
            kafkaProducer.sendMessage("initiatePayment", orderResult);
            log.info("Payment initiation request pushed to Kafka");

            // Send the response to the user, post placing the order
            return mapOrderToOrderSubmitResponse.MapToResponse(orderResult);
        }catch (Exception e){
            log.error("Submit Exception occurred {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<OrderResponse> getOrders(String userName, int pageNumber, int pageSize) {
        // Find the user with the provided username.
        User user = userService.getUser(userName);
        log.info("Found the user for retrieving orders {}", user.getUserName());

        // Query the list of orders for the user.
        Page<Order> pages = orderRepository.findAllByUser(user.getUserId(), PageRequest.of(pageNumber, pageSize).withSort(Sort.Direction.DESC, "updatedAt"));
        log.info(pages);

        // return the list of orders.
        if(pages != null && !pages.isEmpty())
            return pages.map(mapOrderToOrderResponse::MapToResponse).stream().toList();
        return new ArrayList<>();
    }

    @KafkaListener(topics = "paymentStatus", groupId = "kafka")
    @Transactional
    public void getPaymentConfirmation(String resString) throws JsonProcessingException {
        try{
            // Get Payment status from Kafka
            PaymentStatus paymentStatus = objectMapper.readValue(resString, PaymentStatus.class);

            // Find the order received by Kafka and Update the status as Fulfilled/Failure
            Optional<Order> order = orderRepository.findById(paymentStatus.getOrderId());
            log.info("Payment Status received from Kafka {} {} ", paymentStatus.getIsSuccessful(), order.toString());

            if(order.isEmpty()) throw new IdNotFoundException("Order Id not found");

            if(paymentStatus.getIsSuccessful()){
                order.get().setType(OrderStatus.FULFILLED);
                orderRepository.save(order.get());
                log.info("Payment Successful, Order status updated to FULFILLED for {} ", order.toString());
            }else{
                productService.updateQuantity(order.get().getProduct().getProductId(), order.get().getQuantity());
                order.get().setType(OrderStatus.PAYMENT_FAILED);
                orderRepository.save(order.get());
                log.info("Payment Failed, Order status updated to PAYMENT_FAILED for {} ", order.toString());
            }
        }catch (Exception e){
            log.error("============ Exception =============", e);
            throw e;
        }
    }


    private Order validateOrderRequest(Long orderId, OrderOperation orderOperation){
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isEmpty()) throw new IdNotFoundException("Could not find this order");
        if(orderOperation == OrderOperation.EDIT && order.get().getType() != OrderStatus.PENDING) throw new IllegalStateException("Not able perform this action as its either cancelled or fulfilled");
        if(orderOperation != OrderOperation.EDIT && order.get().getType() == OrderStatus.CANCELLED || order.get().getType() == OrderStatus.FULFILLED) throw new IllegalStateException("Not able perform this action as its either cancelled or fulfilled");

        log.info("Order validation successful for {} ", order.toString());
        return order.get();
    }

    private User validatedUser(String userName, Order validatedOrder){
        User user = userService.getUser(userName);
        log.info("Found user {} {}", user.getUserId(), user.getUserName());

        //Check if the user who has created the order, is the one submitting/Deleting.Editing the order.
        if(validatedOrder != null && !Objects.equals(validatedOrder.getUser().getUserId(), user.getUserId()))
            throw new ForbiddenException("You cannot perform this action on this order, Log in as " + validatedOrder.getUser().getUserName() + " to complete the action");
        return user;
    }

    private Product checkIfProductExistsAndIsInStock(Order order){
        // The service method will handle the case where the product was not found.
        Product product = productService.getProductPrice(order.getProduct().getProductId());
        log.info("Found Product {} {}", product.getProductId(), product.getName());

        // Check if the ordered products are in stock
        if(product.getQuantity() < order.getQuantity()){
            log.info("Sorry we do not have enough stock to fulfill this order.");
            throw new IllegalStateException("Sorry we do not have enough stock to fulfill this order.");
        }
        return product;
    }

}
