package com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl;

import com.intuit.orderManagementSystem.order.management.system.entity.Order;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.Interface.GenericEntityToResponseMapper;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderResponse;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Component;

@Component
public class MapOrderToOrderResponse implements GenericEntityToResponseMapper<Order, OrderResponse> {

    @Override
    public OrderResponse MapToResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getOrder_id());
        orderResponse.setProductId(order.getProduct().getProductId());
        orderResponse.setProductName(order.getProduct().getName());
        orderResponse.setOrderStatus(order.getType());
        orderResponse.setQuantity(order.getQuantity());
        return orderResponse;
    }
}