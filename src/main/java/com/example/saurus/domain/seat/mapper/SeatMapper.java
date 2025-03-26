package com.example.saurus.domain.seat.mapper;

import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
import com.example.saurus.domain.seat.dto.request.SeatUpdateRequest;
import com.example.saurus.domain.seat.dto.response.SeatResponse;
import com.example.saurus.domain.seat.entity.Seat;
import com.example.saurus.domain.section.entity.Section;

public class SeatMapper {

    public static Seat toEntity(Section section, SeatCreateRequest request) {
        return Seat.builder()
                .section(section)
                .seatRow(request.getSeatRow())
                .number(request.getNumber().toString())
                .seatType(request.getSeatType())
                .build();
    }

    public static void updateEntity(Seat seat, SeatUpdateRequest request) {
        seat.update(
                request.getRow(),
                request.getNumber().toString(),
                request.getSeatType()
        );
    }

    public static SeatResponse toResponse(Seat seat) {
        return SeatResponse.builder()
                .seatId(seat.getId())
                .sectionId(seat.getSection().getId())
                .row(seat.getSeatRow())
                .number(Integer.parseInt(seat.getNumber()))
                .seatType(seat.getSeatType())
                .build();
    }
}