package com.example.saurus.domain.game.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.game.enums.Teams;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "games")
public class Game extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String place;

    @Nullable
    private String description;

    @Enumerated(EnumType.STRING)
    private Teams opponent;

    private LocalDateTime gameTime;

    private LocalDateTime ticketOpen;
}