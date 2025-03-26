package com.example.saurus.domain.seat.controller;

import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
import com.example.saurus.domain.seat.dto.request.SeatUpdateRequest;
import com.example.saurus.domain.seat.dto.response.SeatResponse;
import com.example.saurus.domain.seat.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games/{gameId}/sections/{sectionId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<SeatResponse> createSeat(
            @PathVariable Long sectionId,
            @RequestBody @Valid SeatCreateRequest request
    ) {
        SeatResponse response = seatService.createSeat(sectionId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{seatId}")
    public ResponseEntity<SeatResponse> updateSeat(
            @PathVariable Long seatId,
            @RequestBody @Valid SeatUpdateRequest request
    ) {
        SeatResponse response = seatService.updateSeat(seatId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{seatId}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable Long sectionId) {
        return ResponseEntity.ok(seatService.getSeatsBySectionId(sectionId));
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<SeatResponse> getSeat(@PathVariable Long seatId) {
        return ResponseEntity.ok(seatService.getSeat(seatId));
    }
}