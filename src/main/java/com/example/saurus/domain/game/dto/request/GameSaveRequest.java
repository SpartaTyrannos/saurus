package com.example.saurus.domain.game.dto.request;

import com.example.saurus.domain.game.enums.Teams;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameSaveRequest {

    @NotBlank(message = "제목은 필수값입니다.")
    private String title;

    @NotBlank(message = "장소는 필수값입니다.")
    private String place;

    @Nullable
    private String description;

    @NotBlank(message = "상대팀은 필수값입니다.")
    private Teams opponent;

    @NotBlank(message = "경기시간은 필수값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gameTime;

    @NotBlank(message = "티켓오픈시간은 필수값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ticketOpen;
}
