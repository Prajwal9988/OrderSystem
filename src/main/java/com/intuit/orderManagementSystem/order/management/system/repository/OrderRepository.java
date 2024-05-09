package com.intuit.orderManagementSystem.order.management.system.repository;

import com.intuit.orderManagementSystem.order.management.system.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o from Order o where o.user.userId=:userId")
    Page<Order> findAllByUser(@Param("userId") Long userId, Pageable pageable);
}
