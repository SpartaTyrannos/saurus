package com.example.saurus.domain.seat.service;

import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
import com.example.saurus.domain.seat.dto.request.SeatUpdateRequest;
import com.example.saurus.domain.seat.dto.response.SeatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SeatService {

    SeatResponse createSeat(AuthUser authUser, Long gameId, Long sectionId, SeatCreateRequest request);

    SeatResponse updateSeat(AuthUser authUser, Long gameId, Long seatId, SeatUpdateRequest request);

    void deleteSeat(AuthUser authUser, Long gameId, Long seatId);

    Page<SeatResponse> getSeatsBySectionId(Long gameId, Long sectionId, Pageable pageable);

    SeatResponse getSeat(Long gameId, Long seatId);

}