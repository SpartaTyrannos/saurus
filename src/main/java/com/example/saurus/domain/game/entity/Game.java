package com.example.saurus.domain.game.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.game.enums.Teams;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table( name = "games",
        indexes = {
                @Index(name = "idx_game_id_deleted", columnList = "id, deleted_at"),
                @Index(name = "idx_game_title_gameTime", columnList = "title, game_time")})
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

    public Game(String title, String place, String description, Teams opponent, LocalDateTime gameTime, LocalDateTime ticketOpen) {
        this.title = title;
        this.place = place;
        this.description = description;
        this.opponent = opponent;
        this.gameTime = gameTime;
        this.ticketOpen = ticketOpen;
    }

    public void updateGame(String title, String place, String description, Teams opponent, LocalDateTime gameTime, LocalDateTime ticketOpen) {
        this.title = title;
        this.place = place;
        this.description = description;
        this.opponent = opponent;
        this.gameTime = gameTime;
        this.ticketOpen = ticketOpen;
    }

}