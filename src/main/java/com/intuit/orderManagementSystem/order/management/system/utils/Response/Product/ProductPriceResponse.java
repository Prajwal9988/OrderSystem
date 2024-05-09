package com.intuit.orderManagementSystem.order.management.system.utils.Response.Product;

import lombok.Data;

@Data
public class ProductPriceResponse {
    private long product_id;
    private String name;
    private String desc;
    private double price;
    private long stock;
}
