package com.example.saurus.domain.payment.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.order.entity.Order;
import com.example.saurus.domain.payment.PaymentMethod;
import com.example.saurus.domain.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "payments")
public class Payment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @Column(name = "final_price")
    private int finalPrice;

    // 결제 수단을 Enum으로 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method",nullable = false)
    private PaymentMethod paymentMethod;

    // 결제 상태를 Enum으로 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status",nullable = false)
    private PaymentStatus paymentStatus;
}
