package com.intuit.orderManagementSystem.order.management.system.repository;

import com.intuit.orderManagementSystem.order.management.system.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContaining(String word, Pageable pageable);
}
