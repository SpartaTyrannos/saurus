package com.example.saurus.domain.payment.repository;

import com.example.saurus.domain.order.entity.Order;
import com.example.saurus.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);
}
