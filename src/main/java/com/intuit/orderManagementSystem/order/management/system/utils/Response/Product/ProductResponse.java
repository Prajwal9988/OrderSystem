package com.intuit.orderManagementSystem.order.management.system.utils.Response.Product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long product_id;
    private String name;
    private int quantity;
}

