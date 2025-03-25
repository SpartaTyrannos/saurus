package com.example.saurus.domain.order.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.payment.entity.Payment;
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
@Table(name = "Orders")
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    private Payment payment;

    private int amount;

    private int total_price;



}
