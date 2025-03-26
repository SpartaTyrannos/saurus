package com.example.saurus.domain.seat.service;

import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
import com.example.saurus.domain.seat.dto.request.SeatUpdateRequest;
import com.example.saurus.domain.seat.dto.response.SeatResponse;

import java.util.List;

public interface SeatService {

    SeatResponse createSeat(Long sectionId, SeatCreateRequest request);

    SeatResponse updateSeat(Long seatId, SeatUpdateRequest request);

    void deleteSeat(Long seatId);

    List<SeatResponse> getSeatsBySectionId(Long sectionId);

    SeatResponse getSeat(Long seatId);
}