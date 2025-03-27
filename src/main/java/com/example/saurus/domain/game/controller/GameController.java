package com.example.saurus.domain.game.controller;

import com.example.saurus.domain.game.dto.request.GameSaveRequest;
import com.example.saurus.domain.game.dto.request.GameUpdateRequest;
import com.example.saurus.domain.game.dto.response.GameResponse;
import com.example.saurus.domain.game.dto.response.GameSaveResponse;
import com.example.saurus.domain.game.dto.response.GameUpdateResponse;
import com.example.saurus.domain.game.dto.response.GamesResponse;
import com.example.saurus.domain.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/games")
    public ResponseEntity<GameSaveResponse> saveGame(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody GameSaveRequest gameSaveRequest
    ) {
        return ResponseEntity.ok(gameService.saveGame(authUser, gameSaveRequest));
    }

    @GetMapping("/games")
    public ResponseEntity<Page<GamesResponse>> findGamesByCondition(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1000-01-01T00:00:00") LocalDateTime startDate,
            @RequestParam(defaultValue = "9999-12-31T23:59:59") LocalDateTime endDate
    ) {
        return ResponseEntity.ok(gameService.findGamesByCondition(authUser, page, size, title, startDate, endDate));
    }

    @GetMapping("/games/{gameId}")
    public ResponseEntity<GameResponse> findGame(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long gameId
    ) {
        return ResponseEntity.ok(gameService.findGame(authUser, gameId));
    }

    @PutMapping("/games/{gameId}")
    public ResponseEntity<GameUpdateResponse> updateGame(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long gameId,
            @Valid @RequestBody GameUpdateRequest gameUpdateRequest
    ) {
        return ResponseEntity.ok(gameService.updateGame(authUser, gameId, gameUpdateRequest));
    }

    @DeleteMapping("/games/{gameId}")
    public void deleteGame(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long gameId
    ) {
        gameService.deleteGame(authUser, gameId);
    }
}
