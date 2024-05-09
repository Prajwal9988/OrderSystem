package com.intuit.orderManagementSystem.order.management.system.controller;


import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.service.serviceInterface.ProductService;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Product.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProduct(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "25") Integer pageSize,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false, defaultValue = "") String searchString
    ){
        log.info("Query Params are {} ,{} ,{} ,{}", pageNumber, pageSize, sortBy, sortDirection);
        List<ProductResponse> productList = this.productService.getProducts(pageNumber, pageSize, sortBy, sortDirection, searchString);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping(value = "/price/{productId}")
    public ResponseEntity<Product> getProductPrice(@PathVariable Long productId) throws IdNotFoundException {
        if(productId == null || productId <= 0) throw new IdNotFoundException("Did not find any product with that ID");
        Product foundProduct = this.productService.getProductPrice(productId);
        return ResponseEntity.ok(foundProduct);
    }

}
