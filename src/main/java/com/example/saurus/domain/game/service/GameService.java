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
import com.example.saurus.domain.seat.entity.Seat;
import com.example.saurus.domain.seat.enums.SeatType;
import com.example.saurus.domain.seat.repository.SeatRepository;
import com.example.saurus.domain.section.entity.Section;
import com.example.saurus.domain.section.repository.SectionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public GameSaveResponseDto saveGame(@Valid GameSaveRequestDto gameSaveRequestDto) {

        Game newGame = new Game(
                gameSaveRequestDto.getTitle(),
                gameSaveRequestDto.getPlace(),
                gameSaveRequestDto.getDescription(),
                gameSaveRequestDto.getOpponent(),
                gameSaveRequestDto.getGameTime(),
                gameSaveRequestDto.getTicketOpen()
        );

        Game savedGame = gameRepository.save(newGame);

        createDefaultSectionsAndSeats(savedGame);

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
        if (title == null || title.isEmpty()) {
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
    public GameUpdateResponseDto updateGame(long gameId, @Valid GameUpdateRequestDto gameUpdateRequestDto) {

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
    public void deleteGame(long gameId) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Game not found"));

        gameRepository.delete(game);
    }

    private void createDefaultSectionsAndSeats(Game game) {
        for (SeatType seatType : SeatType.values()) {
            for (int i = 1; i <= 3; i++) {
                Section section = Section.builder()
                        .game(game)
                        .name(seatType.name() + "-" + i)
                        .type(seatType)
                        .price(getDefaultPriceBySeatType(seatType))
                        .build();

                sectionRepository.save(section);

                createSeatsForSection(section);
            }
        }
    }

    private void createSeatsForSection(Section section) {
        for (char row = 'A'; row <= 'Z'; row++) {
            for (int number = 1; number <= 10; number++) {
                Seat seat = Seat.builder()
                        .section(section)
                        .seatRow(String.valueOf(row))
                        .number(String.valueOf(number))
                        .seatType(section.getType())
                        .build();
                seatRepository.save(seat);
            }
        }
    }

    private int getDefaultPriceBySeatType(SeatType seatType) {
        return switch (seatType) {
            case VIP -> 30000;
            case TABLE -> 25000;
            case EXCITING -> 22000;
            case BLUE -> 20000;
            case ORANGE -> 18000;
            case RED -> 15000;
            case NAVY -> 12000;
            case OUT -> 10000;
        };
    }
}
