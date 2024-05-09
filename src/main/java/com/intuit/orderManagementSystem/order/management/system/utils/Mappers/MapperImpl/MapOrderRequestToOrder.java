package com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl;

import com.intuit.orderManagementSystem.order.management.system.entity.Order;
import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import com.intuit.orderManagementSystem.order.management.system.entity.User;
import com.intuit.orderManagementSystem.order.management.system.enums.OrderStatus;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.Interface.GenericRequestToEntityMapper;
import com.intuit.orderManagementSystem.order.management.system.utils.Request.OrderRequest;
import org.springframework.stereotype.Component;

@Component
public class MapOrderRequestToOrder implements GenericRequestToEntityMapper<Order, OrderRequest> {
    @Override
    public Order RequestToEntity(OrderRequest orderRequest) {
        Order order = new Order();
        order.setPrice(orderRequest.getPrice());
        order.setQuantity(orderRequest.getQuantity());
        order.setType(OrderStatus.PENDING);
        order.setAddress((orderRequest.getAddress()));
        order.setPhoneNumber(orderRequest.getPhone());
        return order;
    }
}
