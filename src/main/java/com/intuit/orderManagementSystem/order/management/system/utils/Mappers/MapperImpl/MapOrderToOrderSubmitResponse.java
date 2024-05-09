package com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl;

import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderSubmitResponse;
import com.intuit.orderManagementSystem.order.management.system.entity.Order;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.Interface.GenericEntityToResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class MapOrderToOrderSubmitResponse implements GenericEntityToResponseMapper<Order, OrderSubmitResponse>{
    @Override
    public OrderSubmitResponse MapToResponse(Order order) {
        OrderSubmitResponse orderSubmitResponse = new OrderSubmitResponse();
        orderSubmitResponse.setOrderId(order.getOrder_id());
        orderSubmitResponse.setOrderStatus(order.getType());
        return orderSubmitResponse;
    }
}
