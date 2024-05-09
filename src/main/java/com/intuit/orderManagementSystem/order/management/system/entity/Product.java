package com.intuit.orderManagementSystem.order.management.system.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;



@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PRODUCT_TABLE")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "productId")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Check(constraints = "quantity > -1")
    private Integer quantity;

    private Double price;

    private String name;

    private String description;

    @Override
    public String toString(){
        return productId + " quantity: " + quantity;
    }
}
