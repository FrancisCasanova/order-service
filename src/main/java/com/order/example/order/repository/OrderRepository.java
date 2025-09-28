package com.order.example.order.repository;


import com.order.example.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByOrderId(String orderId);

    @Query("select o from Order o left join fetch o.items")
    List<Order> findAllWithItems();

    @Query("select o from Order o left join fetch o.items where o.orderId = :orderId")
    Optional<Order> findByOrderIdWithItems(@Param("orderId") String orderId);
}