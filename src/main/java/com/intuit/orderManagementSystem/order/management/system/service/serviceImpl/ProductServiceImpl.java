package com.intuit.orderManagementSystem.order.management.system.service.serviceImpl;
import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.service.serviceInterface.ProductService;
import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import com.intuit.orderManagementSystem.order.management.system.repository.ProductRepository;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl.MapProductToProductResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Product.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MapProductToProductResponse mapProductToProductResponse;

    @Override
    public List<ProductResponse> getProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection, String searchString) {
        Page<Product> pages = this.productRepository.findByNameContaining(searchString, PageRequest.of(pageNumber, pageSize)
                .withSort(Objects.equals(sortDirection, "ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        log.info(pages);
        if(pages != null && !pages.isEmpty())
            return pages.map(mapProductToProductResponse::MapToResponse).stream().toList();
        return new ArrayList<>();
    }

    @Override
    public Product getProductPrice(Long productId) throws IdNotFoundException {
        Optional<Product> product = this.productRepository.findById(productId);
        if(product.isEmpty()) throw new IdNotFoundException("Did not find any product with that ID");
        return product.get();
    }

    @Override
    @Transactional
    public Boolean updateQuantity(Long productId, int orderQuantity){
        try{
            Product product = getProductPrice(productId);
            log.info("Found the product {} : {}", product.getProductId(), product.getName());

            int newQty = product.getQuantity() - orderQuantity;
            log.info("Quantity in stock : {}, Ordered : {}", product.getQuantity(), orderQuantity);
            if(newQty < 0) throw new IllegalStateException("Sorry, this product went out of stock");
            product.setQuantity(newQty);

            log.info("Product quantity updated successfully {} : {}", product.getProductId(), product.getName());
            productRepository.save(product);
            return true;
        }catch (Exception e){
            log.error("Error while decreasing the quantity {}", e.getMessage());
            throw e;
        }
    }
}
