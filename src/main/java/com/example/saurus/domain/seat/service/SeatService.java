//package com.example.saurus.domain.seat.service;
//
//import com.example.saurus.domain.common.dto.AuthUser;
//import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
//import com.example.saurus.domain.seat.dto.request.SeatUpdateRequest;
//import com.example.saurus.domain.seat.dto.response.SeatResponse;
//import com.example.saurus.domain.seat.entity.Seat;
//import com.example.saurus.domain.section.entity.Section;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface SeatService {
//
//    SeatResponse createSeat(AuthUser authUser, Long gameId, Long sectionId, SeatCreateRequest request);
//
//    SeatResponse updateSeat(AuthUser authUser, Long gameId, Long seatId, SeatUpdateRequest request);
//
//    void deleteSeat(AuthUser authUser, Long gameId, Long seatId);
//
//    Page<SeatResponse> getSeatsBySectionId(Long gameId, Long sectionId, Pageable pageable);
//
//    SeatResponse getSeat(Long gameId, Long seatId);
//
//    void createSeatsForSection(Section section);
//
//    void deleteSeatsBySection(Section section);
//
//    Optional<Seat> findByIdAndDeletedAtIsNull(Long seatId);
//
//    boolean existsBySectionIdAndSeatRowAndNumberAndDeletedAtIsNull(Long sectionId, String seatRow, String number);
//}