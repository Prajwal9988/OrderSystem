package com.intuit.orderManagementSystem.order.management.system.service.serviceInterface;

import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Product.ProductResponse;

import java.util.List;

public interface ProductService {
    public List<ProductResponse> getProducts(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortDirection,
            String searchString
    );
    public Product getProductPrice(Long productId) throws IdNotFoundException;
    public Boolean updateQuantity(Long id, int orderQuantity);
}
