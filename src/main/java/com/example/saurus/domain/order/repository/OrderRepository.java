package com.example.saurus.domain.order.repository;

import com.example.saurus.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 주문 다건 조회
    Page<Order> findByUserId(Long userId, Pageable pageable);

    // 주문 단건 조회
    @Query("select o from Order o left join fetch o.tickets where o.id = :orderId")
    Optional<Order> findOrderWithTicketsById(@Param("orderId") Long orderId);
}
