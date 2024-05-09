package com.intuit.orderManagementSystem.order.management.system.service.serviceInterface;

import com.intuit.orderManagementSystem.order.management.system.utils.Request.OrderRequest;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderDeleteResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderSubmitResponse;

import java.util.List;

public interface OrderService {
    public OrderResponse saveOrder(String userName, OrderRequest orderRequest);
    public OrderResponse editOrder(String userName, OrderRequest orderRequest, Long orderId);
    public OrderDeleteResponse deleteOrder(String userName , Long orderId);
    public OrderSubmitResponse submitOrder(String userName, Long orderId);
    public List<OrderResponse> getOrders(String userName, int pageNumber, int pageSize);
}
