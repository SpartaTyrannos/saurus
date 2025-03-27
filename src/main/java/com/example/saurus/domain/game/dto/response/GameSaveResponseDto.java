package com.example.saurus.domain.game.dto.response;

import com.example.saurus.domain.game.enums.Teams;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameSaveResponseDto {

    private final String title;
    private final String place;
    private final String description;
    private final Teams opponent;
    private final LocalDateTime gameTime;
    private final LocalDateTime ticketOpen;

    public GameSaveResponseDto(String title, String place, String description, Teams opponent, LocalDateTime gameTime, LocalDateTime ticketOpen) {
        this.title = title;
        this.place = place;
        this.description = description;
        this.opponent = opponent;
        this.gameTime = gameTime;
        this.ticketOpen = ticketOpen;
    }
}
