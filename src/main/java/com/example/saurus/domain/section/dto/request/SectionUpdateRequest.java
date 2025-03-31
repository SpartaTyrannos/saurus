package com.example.saurus.domain.section.dto.request;

import com.example.saurus.domain.seat.enums.SeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionUpdateRequest {

    @NotBlank(message = "구역 이름은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "좌석 타입은 필수입니다.")
    private SeatType seatType;

    @NotNull(message = "좌석 개수는 필수입니다.")
    @Min(value = 0, message = "개수는 0개 이상이어야 합니다.")
    private Integer count;
}