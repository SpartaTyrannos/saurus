package com.example.saurus.domain.subscribe.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.user.entity.User;
import com.example.saurus.domain.membership.entity.Membership;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name="subscribes")
public class Subscribe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int price;

    private double discount;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    public Subscribe(int price, double discount, LocalDateTime startDate, LocalDateTime endDate, Boolean isActive, User user, Membership membership) {
        this.price = price;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.user = user;
        this.membership = membership;
    }

}
