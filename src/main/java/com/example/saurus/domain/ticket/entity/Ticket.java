package com.example.saurus.domain.ticket.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.order.entity.Order;
import com.example.saurus.domain.seat.entity.Seat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name="subscribes")
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, unique = true)
    private Seat seat;

    public Ticket(Order order, Seat seat) {
        this.order = order;
        this.seat = seat;
    }

}
