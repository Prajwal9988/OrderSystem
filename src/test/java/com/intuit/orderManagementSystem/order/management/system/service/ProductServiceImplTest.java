package com.intuit.orderManagementSystem.order.management.system.service;

import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.repository.ProductRepository;
import com.intuit.orderManagementSystem.order.management.system.service.serviceImpl.ProductServiceImpl;
import com.intuit.orderManagementSystem.order.management.system.utils.Mappers.MapperImpl.MapProductToProductResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Product.ProductResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MapProductToProductResponse mapProductToProductResponse;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @Before
    public void setup() {
        product = new Product(1L, 10, 20.0, "Product", "Description");
    }

    @Test
    public void testGetProducts() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";
        String sortDirection = "ASC";
        String searchString = "Product";

        List<Product> productList = new ArrayList<>();
        productList.add(product);
        Page<Product> page = new PageImpl<>(productList, PageRequest.of(pageNumber, pageSize), productList.size());

        when(productRepository.findByNameContaining(anyString(), any())).thenReturn(page);
        when(mapProductToProductResponse.MapToResponse(any())).thenReturn(new ProductResponse());

        List<ProductResponse> result = productService.getProducts(pageNumber, pageSize, sortBy, sortDirection, searchString);

        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByNameContaining(eq(searchString), any());
    }

    @Test
    public void testGetProductPrice() throws IdNotFoundException {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product result = productService.getProductPrice(productId);

        assertEquals(product, result);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test(expected = IdNotFoundException.class)
    public void testGetProductPriceWithWrongProductId() throws IdNotFoundException {
        Long invalidProductId = 2L;
        when(productRepository.findById(invalidProductId)).thenReturn(Optional.empty());
        productService.getProductPrice(invalidProductId);
    }

    @Test
    public void testUpdateQuantity() {
        Long productId = 1L;
        int orderQuantity = 5;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.updateQuantity(productId, orderQuantity);

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(product);
    }


    @Test(expected = IllegalStateException.class)
    public void testUpdateQuantityWithInsufficientStock() {
        Long productId = 1L;
        int orderQuantity = 15;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        try{
            productService.updateQuantity(productId, orderQuantity);
        }catch (Exception e){
            assertEquals(e.getMessage(), "Sorry, this product went out of stock");
            throw e;
        }
    }

    @Test
    public void testGetProductsReturningEmptyList() {
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";
        String sortDirection = "ASC";
        String searchString = "NonExistentProduct";

        Page<Product> page = Page.empty();
        when(productRepository.findByNameContaining(anyString(), any())).thenReturn(page);

        List<ProductResponse> result = productService.getProducts(pageNumber, pageSize, sortBy, sortDirection, searchString);
        assertEquals(0, result.size());
        verify(productRepository, times(1)).findByNameContaining(eq(searchString), any());
    }

    @Test
    public void testGetProductsWithDescendingOrder() {
        // Arrange
        int pageNumber = 0;
        int pageSize = 10;
        String sortBy = "name";
        String sortDirection = "DESC";
        String searchString = "Product";

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        Page<Product> page = new PageImpl<>(productList, PageRequest.of(pageNumber, pageSize), productList.size());

        when(productRepository.findByNameContaining(anyString(), any())).thenReturn(page);
        when(mapProductToProductResponse.MapToResponse(any())).thenReturn(new ProductResponse());

        List<ProductResponse> result = productService.getProducts(pageNumber, pageSize, sortBy, sortDirection, searchString);

        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByNameContaining(eq(searchString), any());
        verify(mapProductToProductResponse, times(1)).MapToResponse(any());
    }

    @Test
    public void testGetProductPriceWithNullProductId() {
        IdNotFoundException exception = assertThrows(IdNotFoundException.class,
                () -> productService.getProductPrice(null));
        assertEquals("Did not find any product with that ID", exception.getMessage());
    }


    @Test
    public void testUpdateQuantityWithNullProductId() {
        IdNotFoundException exception = assertThrows(IdNotFoundException.class,
                () -> productService.updateQuantity(null, 5));
        assertEquals("Did not find any product with that ID", exception.getMessage());
    }

    @Test(expected = IdNotFoundException.class)
    public void testUpdateQuantityForAWrongProduct() throws IdNotFoundException {
        Long wrongProductId = 2L;
        int orderQuantity = 5;
        when(productRepository.findById(wrongProductId)).thenReturn(Optional.empty());
        productService.updateQuantity(wrongProductId, orderQuantity);
    }
}
