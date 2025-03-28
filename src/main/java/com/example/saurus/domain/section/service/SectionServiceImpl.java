package com.example.saurus.domain.section.service;

import com.example.saurus.domain.common.annotation.Admin;
import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.game.entity.Game;
import com.example.saurus.domain.game.repository.GameRepository;
import com.example.saurus.domain.seat.entity.Seat;
import com.example.saurus.domain.seat.repository.SeatRepository;
import com.example.saurus.domain.section.dto.request.SectionCreateRequest;
import com.example.saurus.domain.section.dto.request.SectionUpdateRequest;
import com.example.saurus.domain.section.dto.response.SectionResponse;
import com.example.saurus.domain.section.entity.Section;
import com.example.saurus.domain.section.mapper.SectionMapper;
import com.example.saurus.domain.section.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional
    public SectionResponse createSection(AuthUser authUser, Long gameId, SectionCreateRequest request) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 경기 정보가 없습니다."));

        if (sectionRepository.existsByGameIdAndNameAndDeletedAtIsNull(gameId, request.getName())) {
            throw new CustomException(HttpStatus.CONFLICT, "같은 이름의 구역이 이미 존재합니다.");
        }

        Section section = SectionMapper.toEntity(game, request);
        section = sectionRepository.save(section);

        createSeatsForSection(section);

        return SectionMapper.toResponse(section);
    }

    @Override
    @Transactional
    public SectionResponse updateSection(AuthUser authUser, Long gameId, Long sectionId, SectionUpdateRequest request) {
        Section section = getSectionWithGameCheck(gameId, sectionId);
        section.update(request.getName(), request.getPrice(), request.getSeatType());
        return SectionMapper.toResponse(section);
    }


    @Override
    @Transactional
    public void deleteSection(AuthUser authUser, Long gameId, Long sectionId) {
        Section section = getSectionWithGameCheck(gameId, sectionId);
        section.delete();
    }

    @Override
    public Page<SectionResponse> getSectionsByGameId(Long gameId, Pageable pageable) {
        return sectionRepository.findByGameIdAndDeletedAtIsNull(gameId, pageable)
                .map(SectionMapper::toResponse);
    }

    @Override
    public SectionResponse getSection(Long gameId, Long sectionId) {
        return SectionMapper.toResponse(getSectionWithGameCheck(gameId, sectionId));
    }

    private Section getActiveSection(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 구역이 존재하지 않거나 삭제되었습니다."));
    }

    private void createSeatsForSection(Section section) {
        List<Seat> seats = new ArrayList<>();

        for (char row = 'A'; row <= 'Z'; row++) {
            for (int number = 1; number <= 10; number++) {
                Seat seat = Seat.builder()
                        .section(section)
                        .seatRow(String.valueOf(row))
                        .number(String.valueOf(number))
                        .seatType(section.getType()) // 🎯 섹션 타입과 동일하게 설정
                        .build();
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
    }

    // 검증
    private Section getSectionWithGameCheck(Long gameId, Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 구역이 존재하지 않거나 삭제되었습니다."));

        if (!section.getGame().getId().equals(gameId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 구역은 지정된 경기의 구역이 아닙니다.");
        }

        return section;
    }
}