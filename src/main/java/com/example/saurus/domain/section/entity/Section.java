package com.example.saurus.domain.section.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.game.entity.Game;
import com.example.saurus.domain.seat.enums.SeatType;
import com.example.saurus.domain.seat.entity.Seat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sections")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatType type;

    public void update(String name, Integer price, SeatType type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }
}