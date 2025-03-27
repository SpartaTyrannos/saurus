package com.example.saurus.domain.game.dto.request;

import com.example.saurus.domain.game.enums.Teams;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameSaveRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String place;

    @Nullable
    private String description;

    @NotBlank
    private Teams opponent;

    @NotBlank
    private LocalDateTime gameTime;

    @NotBlank
    private LocalDateTime ticketOpen;
}
