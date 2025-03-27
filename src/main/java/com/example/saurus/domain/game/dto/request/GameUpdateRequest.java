package com.example.saurus.domain.game.dto.request;

import com.example.saurus.domain.game.enums.Teams;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameUpdateRequest {

    @Nullable
    private String title;

    @Nullable
    private String place;

    @Nullable
    private String description;

    @Nullable
    private Teams opponent;

    @Nullable
    private LocalDateTime gameTime;

    @Nullable
    private LocalDateTime ticketOpen;
}
