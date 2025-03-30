package com.example.saurus.domain.section.service;

import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.game.entity.Game;
import com.example.saurus.domain.game.repository.GameRepository;
import com.example.saurus.domain.seat.enums.SeatType;
//import com.example.saurus.domain.seat.service.SeatService;
import com.example.saurus.domain.section.dto.request.SectionCreateRequest;
import com.example.saurus.domain.section.dto.request.SectionUpdateRequest;
import com.example.saurus.domain.section.dto.response.SectionResponse;
import com.example.saurus.domain.section.entity.Section;
import com.example.saurus.domain.section.mapper.SectionMapper;
import com.example.saurus.domain.section.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final GameRepository gameRepository;
//    private final SeatService seatService;

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

        // 좌석 생성 위임
//        seatService.createSeatsForSection(section);

        return SectionMapper.toResponse(section);
    }

    @Override
    @Transactional
    public SectionResponse updateSection(AuthUser authUser, Long gameId, Long sectionId, SectionUpdateRequest request) {
        Section section = getSectionWithGameCheck(gameId, sectionId);
        section.update(request.getName(), request.getPrice(), request.getSeatType(), request.getPrice());
        return SectionMapper.toResponse(section);
    }

    @Override
    @Transactional
    public void deleteSection(AuthUser authUser, Long gameId, Long sectionId) {
        Section section = getSectionWithGameCheck(gameId, sectionId);
        section.delete();

    }

    @Transactional
    public void deleteSectionsByGame(Game game) {
        List<Section> sections = sectionRepository.findByGameIdAndDeletedAtIsNull(game.getId());

        for (Section section : sections) {
            section.delete();

            // Seat 삭제는 SeatService에 위임
//            seatService.deleteSeatsBySection(section);
        }
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

    @Override
    @Transactional
    public void createDefaultSectionsAndSeats(Game game) {
        List<SeatType> types = List.of(SeatType.values());

        for (SeatType type : SeatType.values()) {
            for (int i = 1; i <= 3; i++) {
                Section section = Section.builder()
                        .game(game)
                        .name(type.name() + " 구역 " + i)
                        .type(type)
                        .price(getDefaultPrice(type))
                        .count(100)
                        .build();
                section = sectionRepository.save(section);

//                seatService.createSeatsForSection(section);
            }
        }
    }

    private int getDefaultPrice(SeatType seatType) {
        return switch (seatType) {
            case VIP -> 30000;
            case ORANGE -> 25000;
            case NAVY -> 22000;
            default -> 20000;
        };
    }


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