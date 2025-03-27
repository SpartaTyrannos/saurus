package com.example.saurus.domain.game.dto.response;

import com.example.saurus.domain.game.enums.Teams;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameUpdateResponse {

    private final String title;
    private final String place;
    private final String description;
    private final String opponent;
    private final LocalDateTime gameTime;
    private final LocalDateTime ticketOpen;


    public GameUpdateResponse(String title, String place, String description, Teams opponent, LocalDateTime gameTime, LocalDateTime ticketOpen) {
        this.title = title;
        this.place = place;
        this.description = description;
        this.opponent = opponent.toString();
        this.gameTime = gameTime;
        this.ticketOpen = ticketOpen;
    }
}
