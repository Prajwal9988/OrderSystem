package com.intuit.orderManagementSystem.order.management.system.controller;

import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.service.serviceInterface.ProductService;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Product.ProductResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    public void getProduct() {
        List<ProductResponse> productList = new ArrayList<>();
        when(productService.getProducts(anyInt(), anyInt(), anyString(), anyString(), anyString())).thenReturn(productList);
        ResponseEntity<List<ProductResponse>> responseEntity = productController.getProduct(0, 25, "name", "ASC", "");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(productList, responseEntity.getBody());
    }

    @Test
    public void getProductPriceWithValidProductId() throws IdNotFoundException {
        long productId = 1L;
        Product product = new Product();
        when(productService.getProductPrice(productId)).thenReturn(product);
        ResponseEntity<Product> responseEntity = productController.getProductPrice(productId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(product, responseEntity.getBody());
    }

    @Test
    public void getProductPriceWithInvalidProductId() {
        long productId = 0L;
        try {
            productController.getProductPrice(productId);
        } catch (IdNotFoundException e) {
            assertEquals("Did not find any product with that ID", e.getMessage());
        }
    }
}
