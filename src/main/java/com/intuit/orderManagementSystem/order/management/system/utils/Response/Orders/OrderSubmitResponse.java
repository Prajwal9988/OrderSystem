package com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders;


import com.intuit.orderManagementSystem.order.management.system.enums.OrderStatus;
import com.intuit.orderManagementSystem.order.management.system.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSubmitResponse {
    private Long orderId;
    private OrderStatus orderStatus;
}
