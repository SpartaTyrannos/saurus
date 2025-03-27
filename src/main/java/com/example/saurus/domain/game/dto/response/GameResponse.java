package com.example.saurus.domain.game.dto.response;

import com.example.saurus.domain.game.enums.Teams;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameResponse {

    private final Long id;
    private final String title;
    private final String place;
    private final String opponent;
    private final LocalDateTime gameTime;
    private final LocalDateTime ticketOpen;


    public GameResponse(Long id, String title, String place, Teams opponent, LocalDateTime gameTime, LocalDateTime ticketOpen) {
        this.id = id;
        this.title = title;
        this.place = place;
        this.opponent = opponent.toString();
        this.gameTime = gameTime;
        this.ticketOpen = ticketOpen;
    }
}
