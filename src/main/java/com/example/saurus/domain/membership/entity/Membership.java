package com.example.saurus.domain.membership.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name="memberships")
public class Membership extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer price;

    private Double discount;

    private Integer year;

    public Membership(String name, Integer price, Double discount, Integer year) {
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.year = year;
    }

    public void update(String name, Integer price, Double discount, Integer year) {
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.year = year;
    }
}
