package com.example.saurus.domain.game.controller;

import com.example.saurus.domain.common.annotation.Admin;
import com.example.saurus.domain.game.dto.request.GameSaveRequestDto;
import com.example.saurus.domain.game.dto.request.GameUpdateRequestDto;
import com.example.saurus.domain.game.dto.response.GameResponseDto;
import com.example.saurus.domain.game.dto.response.GameSaveResponseDto;
import com.example.saurus.domain.game.dto.response.GameUpdateResponseDto;
import com.example.saurus.domain.game.dto.response.GamesResponseDto;
import com.example.saurus.domain.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @Admin
    @PostMapping
    public ResponseEntity<GameSaveResponseDto> saveGame(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody GameSaveRequestDto gameSaveRequestDto
    ) {
        return ResponseEntity.ok(gameService.saveGame(gameSaveRequestDto));
    }

    @GetMapping
    public ResponseEntity<Page<GamesResponseDto>> findGamesByCondition(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1000-01-01T00:00:00") LocalDateTime startDate,
            @RequestParam(defaultValue = "9999-12-31T23:59:59") LocalDateTime endDate
    ) {
        return ResponseEntity.ok(gameService.findGamesByCondition(page, size, title, startDate, endDate));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponseDto> findGame(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long gameId
    ) {
        return ResponseEntity.ok(gameService.findGame(gameId));
    }

    @Admin
    @PutMapping("/{gameId}")
    public ResponseEntity<GameUpdateResponseDto> updateGame(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long gameId,
            @Valid @RequestBody GameUpdateRequestDto gameUpdateRequestDto
    ) {
        return ResponseEntity.ok(gameService.updateGame(gameId, gameUpdateRequestDto));
    }

    @Admin
    @DeleteMapping("/{gameId}")
    public void deleteGame(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long gameId
    ) {
        gameService.deleteGame(gameId);
    }
}
