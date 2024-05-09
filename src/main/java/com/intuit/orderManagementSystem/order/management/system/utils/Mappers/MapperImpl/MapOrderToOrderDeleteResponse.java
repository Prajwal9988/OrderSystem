package com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl;

import com.intuit.orderManagementSystem.order.management.system.entity.Order;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.Interface.GenericEntityToResponseMapper;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderDeleteResponse;
import org.springframework.stereotype.Component;

@Component
public class MapOrderToOrderDeleteResponse implements GenericEntityToResponseMapper<Order, OrderDeleteResponse> {

    @Override
    public OrderDeleteResponse MapToResponse(Order order) {
        return null;
    }
}
