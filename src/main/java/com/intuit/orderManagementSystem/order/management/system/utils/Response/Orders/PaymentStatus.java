package com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intuit.orderManagementSystem.order.management.system.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class PaymentStatus {
    private Long orderId;
    private Integer quantity;
    private Double price;
    private Boolean isSuccessful;
}
