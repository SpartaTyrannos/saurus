package com.example.saurus.domain.section.dto.response;

import com.example.saurus.domain.seat.enums.SeatType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionResponse {

    private Long id;
    private Long gameId;
    private String name;
    private Integer price;
    private SeatType seatType;
    private Integer count;
}