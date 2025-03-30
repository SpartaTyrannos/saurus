package com.example.saurus.domain.order.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.order.OrderStatus;
import com.example.saurus.domain.payment.entity.Payment;
import com.example.saurus.domain.ticket.entity.Ticket;
import com.example.saurus.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "orders",
        indexes = {
                @Index(name = "idx_order_user_created", columnList = "user_id, created_at DESC")})
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    private Payment payment;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    private int ticketAmount;

    private int totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

}
