package com.example.saurus.domain.game.service;

import com.example.saurus.domain.game.dto.request.GameSaveRequest;
import com.example.saurus.domain.game.dto.request.GameUpdateRequest;
import com.example.saurus.domain.game.dto.response.GameResponse;
import com.example.saurus.domain.game.dto.response.GameSaveResponse;
import com.example.saurus.domain.game.dto.response.GameUpdateResponse;
import com.example.saurus.domain.game.dto.response.GamesResponse;
import com.example.saurus.domain.game.entity.Game;
import com.example.saurus.domain.game.repository.GameRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    @Transactional
    public GameSaveResponse saveGame(AuthUser authUser, @Valid GameSaveRequest gameSaveRequest) {

        User user = User.fromAuthUser(authUser);

        if (!ObjectUtils.nullSafeEquals(user.getRole(), "ADMIN")) {
            throw new CustomException("Permission denied");
        }

        Game newGame = new Game(
                gameSaveRequest.getTitle(),
                gameSaveRequest.getPlace(),
                gameSaveRequest.getDescription(),
                gameSaveRequest.getOpponent(),
                gameSaveRequest.getGameTime(),
                gameSaveRequest.getTicketOpen()
                );

        Game savedGame = gameRepository.save(newGame);
        return new GameSaveResponse(
                savedGame.getTitle(),
                savedGame.getPlace(),
                savedGame.getDescription(),
                savedGame.getOpponent(),
                savedGame.getGameTime(),
                savedGame.getTicketOpen()
        );
    }

    @Transactional(readOnly = true)
    public Page<GamesResponse> findGamesByCondition(AuthUser authUser, int page, int size, String title, LocalDateTime startDate, LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page - 1, size);

        User user = User.fromAuthUser(authUser);

        Page<Game> games;
        if (title.isEmpty()) {
            games = gameRepository.findAllByDate(pageable, startDate, endDate);
        } else {
            games = gameRepository.findAllByTitleAndDate(pageable, title, startDate, endDate);
        }

        return games.map(game -> new GamesResponse(
                game.getId(),
                game.getTitle(),
                game.getPlace(),
                game.getOpponent(),
                game.getGameTime()
        ));
    }

    @Transactional(readOnly = true)
    public GameResponse findGame(AuthUser authUser, long gameId) {

        User user = User.fromAuthUser(authUser);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException("Game not found"));

        return new GameResponse(
                game.getId(),
                game.getTitle(),
                game.getPlace(),
                game.getOpponent(),
                game.getGameTime(),
                game.getTicketOpen()
        );
    }

    @Transactional
    public GameUpdateResponse updateGame(AuthUser authUser, long gameId, @Valid GameUpdateRequest gameUpdateRequest) {

        User user = User.fromAuthUser(authUser);

        if (!ObjectUtils.nullSafeEquals(user.getRole(), "ADMIN")) {
            throw new CustomException("Permission denied");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException("Game not found"));

        game.updateGame(
                gameUpdateRequest.getTitle(),
                gameUpdateRequest.getPlace(),
                gameUpdateRequest.getDescription(),
                gameUpdateRequest.getOpponent(),
                gameUpdateRequest.getGameTime(),
                gameUpdateRequest.getTicketOpen()
        );

        return new GameUpdateResponse(
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

        User user = User.fromAuthUser(authUser);

        if (!ObjectUtils.nullSafeEquals(user.getRole(), "ADMIN")) {
            throw new CustomException("Permission denied");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException("Game not found"));

        gameRepository.delete(game);
    }
}
