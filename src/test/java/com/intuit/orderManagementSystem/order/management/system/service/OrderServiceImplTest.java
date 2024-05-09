package com.intuit.orderManagementSystem.order.management.system.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.orderManagementSystem.order.management.system.entity.Order;
import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import com.intuit.orderManagementSystem.order.management.system.entity.User;
import com.intuit.orderManagementSystem.order.management.system.enums.OrderStatus;
import com.intuit.orderManagementSystem.order.management.system.exception.ForbiddenException;
import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.repository.OrderRepository;
import com.intuit.orderManagementSystem.order.management.system.service.serviceImpl.OrderServiceImpl;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

    private static final String TEST_USER_NAME = "TestUser";
    private static final long PRODUCT_ID = 1L;
    private static final long ORDER_ID = 1L;
    private User user;
    private Order order;
    private Product product;

    @Mock
    UserService userService;

    @Mock
    ProductService productService;

    @Mock
    MapOrderRequestToOrder mapOrderRequestToOrder;

    @Mock
    MapOrderToOrderResponse mapOrderToOrderResponse;

    @Mock
    MapOrderToOrderSubmitResponse mapOrderToOrderSubmitResponse;

    @Mock
    OrderRepository orderRepository;

    @Mock
    KafkaProducer kafkaProducer;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    OrderServiceImpl orderService;

    @Before
    public void init(){
        user = new User(1L,TEST_USER_NAME,"Test","User","9988998899","XYZ");
        product = new Product(PRODUCT_ID,10,20.0,"Product","Desc");
        order = new Order();
        order.setOrder_id(1L);
        order.setQuantity(5);
        order.setPrice(20.0);
        order.setProduct(product);
        order.setUser(user);
        order.setType(OrderStatus.PENDING);
    }


    @Test
    public void testSaveOrder(){
        when(userService.getUser(any(String.class))).thenReturn(user);
        when(productService.getProductPrice(any(Long.class))).thenReturn(product);
        when(mapOrderRequestToOrder.RequestToEntity(any(OrderRequest.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(mapOrderToOrderResponse.MapToResponse(order)).thenReturn(new OrderResponse());

        OrderResponse result = orderService.saveOrder(TEST_USER_NAME, new OrderRequest(PRODUCT_ID, 10.0, 2, "XYZ", "99999"));
        assertEquals(result, new OrderResponse());
        verify(orderRepository, times(1)).save(order);
    }

    @Test(expected = IdNotFoundException.class)
    public void testSaveOrderWithNoUser(){
        when(userService.getUser(any(String.class))).thenThrow(new IdNotFoundException("User with this username not found"));

        OrderResponse result = orderService.saveOrder(TEST_USER_NAME, new OrderRequest(PRODUCT_ID, 10.0, 2, "XYZ", "99999"));
        verify(orderRepository, times(0)).save(order);
    }

    @Test(expected = IdNotFoundException.class)
    public void testSaveOrderWithNoProduct(){
        when(userService.getUser(any(String.class))).thenReturn(user);
        when(productService.getProductPrice(any(Long.class))).thenThrow(new IdNotFoundException("Did not find any product with that ID"));

        OrderResponse result = orderService.saveOrder(TEST_USER_NAME, new OrderRequest(PRODUCT_ID, 10.0, 2, "XYZ", "99999"));
        verify(orderRepository, times(0)).save(order);
    }

    @Test
    public void testEditingAnOrder(){
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(userService.getUser(any(String.class))).thenReturn(user);
        when(productService.getProductPrice(any(Long.class))).thenReturn(product);
        when(mapOrderRequestToOrder.RequestToEntity(any(OrderRequest.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(mapOrderToOrderResponse.MapToResponse(order)).thenReturn(new OrderResponse());

        OrderResponse result = orderService.editOrder(TEST_USER_NAME, new OrderRequest(PRODUCT_ID, 10.0, 2, "XYZ", "99999"), 1L);
        assertEquals(result, new OrderResponse());
        verify(orderRepository, times(1)).save(order);
    }

    @Test(expected = ForbiddenException.class)
    public void testEditingAnOrderWithDifferentUser(){
        User returnUser = new User(2L, "Wrong_User", "Wrong", "User", "111111", "ABC");
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(userService.getUser(any(String.class))).thenReturn(returnUser);

        try{
            OrderResponse result = orderService.editOrder(TEST_USER_NAME, new OrderRequest(PRODUCT_ID, 10.0, 2, "XYZ", "99999"), 1L);
        }catch(ForbiddenException e){
            assertEquals(e.getMessage(), "You cannot perform this action on this order, Log in as " + TEST_USER_NAME + " to complete the action");
            verify(orderRepository, times(0)).save(order);
            throw  e;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testEditingAnOrderWhenProductIsOutOfStock(){
        order.setQuantity(11);
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(userService.getUser(any(String.class))).thenReturn(user);
        when(productService.getProductPrice(any(Long.class))).thenReturn(product);
        when(mapOrderRequestToOrder.RequestToEntity(any(OrderRequest.class))).thenReturn(order);

        try{
            OrderResponse result = orderService.editOrder(TEST_USER_NAME, new OrderRequest(PRODUCT_ID, 10.0, 2, "XYZ", "99999"), 1L);
        }catch(IllegalStateException e){
            assertEquals(e.getMessage(), "Sorry we do not have enough stock to fulfill this order.");
            verify(orderRepository, times(0)).save(order);
            throw  e;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testEditingAnOrderWhichIsAlreadyPlacedOrCancelled(){
        order.setType(OrderStatus.CANCELLED);

        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));

        try{
            OrderResponse result = orderService.editOrder(TEST_USER_NAME, new OrderRequest(PRODUCT_ID, 10.0, 2, "XYZ", "99999"), 1L);
        }catch(IllegalStateException e){
            assertEquals(e.getMessage(), "Not able perform this action as its either cancelled or fulfilled");
            verify(orderRepository, times(0)).save(order);
            throw  e;
        }
    }

    @Test(expected = IdNotFoundException.class)
    public void testEditingAnOrderWhenOrderIsEmpty(){

        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        try{
            OrderResponse result = orderService.editOrder(TEST_USER_NAME, new OrderRequest(PRODUCT_ID, 10.0, 2, "XYZ", "99999"), 1L);
        }catch(IdNotFoundException e){
            assertEquals(e.getMessage(), "Could not find this order");
            verify(orderRepository, times(0)).save(order);
            throw  e;
        }
    }

    @Test
    public void testDeletingOrder(){
        order.setType(OrderStatus.PAYMENT_FAILED);
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(userService.getUser(any(String.class))).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDeleteResponse orderDeleteResponse = orderService.deleteOrder(TEST_USER_NAME,  1L);
        assertEquals(orderDeleteResponse, new OrderDeleteResponse("Deleted Successfully"));
        verify(orderRepository, times(1)).save(order);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeletingFulfilledOrCancelledOrder(){
        order.setType(OrderStatus.CANCELLED);
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));

        try{
            OrderDeleteResponse orderDeleteResponse = orderService.deleteOrder(TEST_USER_NAME,  1L);
        }
        catch(IllegalStateException e){
            assertEquals(e.getMessage(), "Not able perform this action as its either cancelled or fulfilled");
            verify(orderRepository, times(0)).save(order);
            throw e;
        }
    }

    @Test(expected = ForbiddenException.class)
    public void testDeletingOrderWithWrongUser(){
        User returnUser = new User(2L, "Wrong_User", "Wrong", "User", "111111", "ABC");
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(userService.getUser(any(String.class))).thenReturn(returnUser);

        try{
            OrderDeleteResponse orderDeleteResponse = orderService.deleteOrder(TEST_USER_NAME,  1L);
        }
        catch(ForbiddenException e){
            assertEquals(e.getMessage(), "You cannot perform this action on this order, Log in as " + TEST_USER_NAME + " to complete the action");
            verify(orderRepository, times(0)).save(order);
            throw e;
        }
    }


    @Test
    public void testSubmitOrder(){
        order.setUser(user);
        order.setProduct(product);

        when(userService.getUser(any(String.class))).thenReturn(user);
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(mapOrderToOrderSubmitResponse.MapToResponse(order)).thenReturn(new OrderSubmitResponse());

        OrderSubmitResponse result = orderService.submitOrder(TEST_USER_NAME, 1L);
        assertEquals(result, new OrderSubmitResponse());
        verify(productService,times(1)).updateQuantity(1L, 5);
        verify(orderRepository, times(1)).save(order);
        verify(kafkaProducer, times(1)).sendMessage("initiatePayment", order);
    }

    @Test(expected = ForbiddenException.class)
    public void testSubmitOrderWithDifferentUser(){
        User returnUser = new User(2L, "Wrong_User", "Wrong", "User", "111111", "ABC");

        order.setUser(user);
        order.setProduct(product);

        when(userService.getUser(any(String.class))).thenReturn(returnUser);
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));

        try{
            OrderSubmitResponse result = orderService.submitOrder(TEST_USER_NAME, 1L);
        }catch (ForbiddenException e){
            verify(productService,times(0)).updateQuantity(1L, 5);
            verify(orderRepository, times(0)).save(order);
            verify(kafkaProducer, times(0)).sendMessage("initiatePayment", order);
            assertEquals(e.getMessage(), "You cannot perform this action on this order, Log in as " + TEST_USER_NAME + " to complete the action");
            throw e;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testSubmitOrderWhenProductIsOutOfStock(){
        order.setUser(user);
        order.setProduct(product);

        when(userService.getUser(any(String.class))).thenReturn(user);
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        when(productService.updateQuantity(1L, 5)).thenThrow(new IllegalStateException("Sorry, this product went out of stock"));

        try{
            OrderSubmitResponse result = orderService.submitOrder(TEST_USER_NAME, 1L);
        }catch (IllegalStateException e){
            verify(productService,times(1)).updateQuantity(1L, 5);
            verify(orderRepository, times(0)).save(order);
            verify(kafkaProducer, times(0)).sendMessage("initiatePayment", order);
            assertEquals(e.getMessage(),  "Sorry, this product went out of stock");
            throw e;
        }
    }

    @Test
    public void testGetOrder(){
        order.setProduct(product); order.setUser(user);
        long currentTimeMillis = System.currentTimeMillis();
        long yesterdayTimeMillis = currentTimeMillis - (24 * 60 * 60 * 1000);
        Date yesterday = new Date(yesterdayTimeMillis);
        order.setUpdatedAt(yesterday);
        List<Order> orderList = new ArrayList<>(List.of(order));
        Page<Order> page = new PageImpl<>(orderList, PageRequest.of(0, 50).withSort(Sort.Direction.DESC, "updatedAt"), orderList.size());

        when(userService.getUser(any(String.class))).thenReturn(user);
        when(orderRepository.findAllByUser(1L, PageRequest.of(0, 50).withSort(Sort.Direction.DESC, "updatedAt"))).thenReturn(page);
        when(mapOrderToOrderResponse.MapToResponse(order)).thenReturn(new OrderResponse(order.getOrder_id(),order.getProduct().getProductId(),order.getProduct().getName(),order.getType(), order.getQuantity()));

        List<OrderResponse> resultList = orderService.getOrders(TEST_USER_NAME, 0, 50);
        List<OrderResponse> compareList = new ArrayList<>(List.of(new OrderResponse(order.getOrder_id(),order.getProduct().getProductId(),order.getProduct().getName(),order.getType(), order.getQuantity())));
        assertEquals(resultList, compareList);
        verify(mapOrderToOrderResponse, times(orderList.size())).MapToResponse(order);
    }

    @Test
    public void testGetOrderWithEmptyResponse(){
        order.setProduct(product); order.setUser(user);
        List<Order> orderList = new ArrayList<>();
        Page<Order> page = new PageImpl<>(orderList, PageRequest.of(0, 50), 0);

        when(userService.getUser(any(String.class))).thenReturn(user);
        // when(orderRepository.findAllByUser(1L, PageRequest.of(0, 50))).thenReturn(page);

        List<OrderResponse> resultList = orderService.getOrders(TEST_USER_NAME, 0, 50);
        assertEquals(resultList, new ArrayList<>());
        verify(mapOrderToOrderResponse, times(0)).MapToResponse(order);
    }

    @Test
    public void testGetPaymentConfirmationFromKafkaSuccess() throws JsonProcessingException {
            PaymentStatus paymentStatus = new PaymentStatus(1L,10,10.0,true);
            when(objectMapper.readValue("", PaymentStatus.class)).thenReturn(paymentStatus);
            when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
            orderService.getPaymentConfirmation("");
            verify(orderRepository, times(1)).save(order);
        verify(productService, times(0)).updateQuantity(1L, 5);

    }

    @Test
    public void testGetPaymentConfirmationFromKafkaFailure() throws JsonProcessingException {
        PaymentStatus paymentStatus = new PaymentStatus(1L,10,10.0,false);
        when(objectMapper.readValue("", PaymentStatus.class)).thenReturn(paymentStatus);
        when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
        orderService.getPaymentConfirmation("");
        verify(orderRepository, times(1)).save(order);
        verify(productService, times(1)).updateQuantity(1L, 5);
    }
}


