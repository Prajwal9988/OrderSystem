package com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders;

import com.intuit.orderManagementSystem.order.management.system.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long productId;
    private String productName;
    private OrderStatus orderStatus;
    private Integer quantity;
}
