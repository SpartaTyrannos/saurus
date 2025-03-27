package com.example.saurus.domain.seat.service;

import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
import com.example.saurus.domain.seat.dto.request.SeatUpdateRequest;
import com.example.saurus.domain.seat.dto.response.SeatResponse;

import java.util.List;

public interface SeatService {

    SeatResponse createSeat(AuthUser authUser, Long sectionId, SeatCreateRequest request);

    SeatResponse updateSeat(AuthUser authUser, Long seatId, SeatUpdateRequest request);

    void deleteSeat(AuthUser authUser, Long seatId);

    List<SeatResponse> getSeatsBySectionId(Long sectionId);

    SeatResponse getSeat(Long seatId);
}