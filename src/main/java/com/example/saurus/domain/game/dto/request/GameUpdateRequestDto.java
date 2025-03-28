package com.example.saurus.domain.game.dto.request;

import com.example.saurus.domain.game.enums.Teams;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameUpdateRequestDto {

    @NotBlank(message = "제목은 필수값입니다.")
    private String title;

    @NotBlank(message = "장소는 필수값입니다.")
    private String place;

    @Nullable
    private String description;

    @NotNull(message = "상대팀은 필수값입니다.")
    private Teams opponent;

    @NotNull(message = "경기시간은 필수값입니다.")
    private LocalDateTime gameTime;

    @NotNull(message = "티켓오픈시간은 필수값입니다.")
    private LocalDateTime ticketOpen;
}
