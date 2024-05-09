package com.intuit.orderManagementSystem.order.management.system.utils.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @NotNull(message = "quantity cannot be null")
    private Long productId;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price can have up to 10 digits in total, with 2 fractional digits")
    private Double price;

    @NotNull(message = "quantity cannot be null")
    @Min(value = 1, message = "quantity must be greater than or equal to 1")
    private Integer quantity;

    @NotNull(message = "address cannot be null")
    private String address;

    @NotNull(message = "phone cannot be null")
    private String phone;
}
