package com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl;

import com.intuit.orderManagementSystem.order.management.system.exception.UnexpectedException;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Product.ProductResponse;
import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.Interface.GenericEntityToResponseMapper;
import org.springframework.stereotype.Component;


@Component
public class MapProductToProductResponse implements GenericEntityToResponseMapper<Product, ProductResponse> {
    @Override
    public ProductResponse MapToResponse(Product product) {
        if(product==null) throw new UnexpectedException("product is null");
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct_id(product.getProductId());
        productResponse.setName(product.getName());
        productResponse.setQuantity(product.getQuantity());
        return productResponse;
    }
}
