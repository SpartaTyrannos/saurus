package com.example.saurus.domain.seat.controller;

import com.example.saurus.domain.common.annotation.Admin;
import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
import com.example.saurus.domain.seat.dto.request.SeatUpdateRequest;
import com.example.saurus.domain.seat.dto.response.SeatResponse;
import com.example.saurus.domain.seat.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games/{gameId}/sections/{sectionId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @Admin
    @PostMapping
    public ResponseEntity<SeatResponse> createSeat(
            @PathVariable Long gameId,
            @PathVariable Long sectionId,
            @RequestBody @Valid SeatCreateRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(seatService.createSeat(authUser, gameId, sectionId, request));
    }

    @Admin
    @PutMapping("/{seatId}")
    public ResponseEntity<SeatResponse> updateSeat(
            @PathVariable Long gameId,
            @PathVariable Long sectionId,
            @PathVariable Long seatId,
            @RequestBody @Valid SeatUpdateRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(seatService.updateSeat(authUser, gameId, seatId, request));
    }

    @Admin
    @DeleteMapping("/{seatId}")
    public ResponseEntity<Void> deleteSeat(
            @PathVariable Long gameId,
            @PathVariable Long sectionId,
            @PathVariable Long seatId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        seatService.deleteSeat(authUser, gameId, seatId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<SeatResponse>> getSeats(
            @PathVariable Long gameId,
            @PathVariable Long sectionId,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(seatService.getSeatsBySectionId(gameId, sectionId, pageable));
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<SeatResponse> getSeat(
            @PathVariable Long gameId,
            @PathVariable Long sectionId,
            @PathVariable Long seatId
    ) {
        return ResponseEntity.ok(seatService.getSeat(gameId, seatId));
    }
}