package com.example.saurus.domain.ticket.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.order.entity.Order;
import com.example.saurus.domain.section.entity.Section;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table( name="tickets")
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    public Ticket(Order order, Section section) {
        this.order = order;
        this.section = section;
    }

}
