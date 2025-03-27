package com.example.saurus.domain.game.service;

import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.game.dto.request.GameSaveRequestDto;
import com.example.saurus.domain.game.dto.request.GameUpdateRequestDto;
import com.example.saurus.domain.game.dto.response.GameResponseDto;
import com.example.saurus.domain.game.dto.response.GameSaveResponseDto;
import com.example.saurus.domain.game.dto.response.GameUpdateResponseDto;
import com.example.saurus.domain.game.dto.response.GamesResponseDto;
import com.example.saurus.domain.game.entity.Game;
import com.example.saurus.domain.game.repository.GameRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    @Transactional
    public GameSaveResponseDto saveGame(AuthUser authUser, @Valid GameSaveRequestDto gameSaveRequestDto) {

        Game newGame = new Game(
                gameSaveRequestDto.getTitle(),
                gameSaveRequestDto.getPlace(),
                gameSaveRequestDto.getDescription(),
                gameSaveRequestDto.getOpponent(),
                gameSaveRequestDto.getGameTime(),
                gameSaveRequestDto.getTicketOpen()
                );

        Game savedGame = gameRepository.save(newGame);
        return new GameSaveResponseDto(
                savedGame.getTitle(),
                savedGame.getPlace(),
                savedGame.getDescription(),
                savedGame.getOpponent(),
                savedGame.getGameTime(),
                savedGame.getTicketOpen()
        );
    }

    @Transactional(readOnly = true)
    public Page<GamesResponseDto> findGamesByCondition(int page, int size, String title, LocalDateTime startDate, LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Game> games;
        if (title.isEmpty()) {
            games = gameRepository.findAllByDate(pageable, startDate, endDate);
        } else {
            games = gameRepository.findAllByTitleAndDate(pageable, title, startDate, endDate);
        }

        return games.map(game -> new GamesResponseDto(
                game.getId(),
                game.getTitle(),
                game.getPlace(),
                game.getOpponent(),
                game.getGameTime()
        ));
    }

    @Transactional(readOnly = true)
    public GameResponseDto findGame(long gameId) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Game not found"));

        return new GameResponseDto(
                game.getId(),
                game.getTitle(),
                game.getPlace(),
                game.getOpponent(),
                game.getGameTime(),
                game.getTicketOpen()
        );
    }

    @Transactional
    public GameUpdateResponseDto updateGame(AuthUser authUser, long gameId, @Valid GameUpdateRequestDto gameUpdateRequestDto) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Game not found"));

        game.updateGame(
                gameUpdateRequestDto.getTitle(),
                gameUpdateRequestDto.getPlace(),
                gameUpdateRequestDto.getDescription(),
                gameUpdateRequestDto.getOpponent(),
                gameUpdateRequestDto.getGameTime(),
                gameUpdateRequestDto.getTicketOpen()
        );

        return new GameUpdateResponseDto(
                game.getTitle(),
                game.getPlace(),
                game.getDescription(),
                game.getOpponent(),
                game.getGameTime(),
                game.getTicketOpen()
        );
    }

    @Transactional
    public void deleteGame(AuthUser authUser, long gameId) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Game not found"));

        gameRepository.delete(game);
    }
}
