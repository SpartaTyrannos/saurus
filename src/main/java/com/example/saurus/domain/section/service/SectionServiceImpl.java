package com.example.saurus.domain.section.service;

import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.game.entity.Game;
import com.example.saurus.domain.game.repository.GameRepository;
import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
import com.example.saurus.domain.seat.dto.response.SeatResponse;
import com.example.saurus.domain.seat.entity.Seat;
import com.example.saurus.domain.seat.mapper.SeatMapper;
import com.example.saurus.domain.section.dto.request.SectionCreateRequest;
import com.example.saurus.domain.section.dto.request.SectionUpdateRequest;
import com.example.saurus.domain.section.dto.response.SectionResponse;
import com.example.saurus.domain.section.entity.Section;
import com.example.saurus.domain.section.mapper.SectionMapper;
import com.example.saurus.domain.section.repository.SectionRepository;
import jakarta.persistence.EntityNotFoundException;
import static java.util.stream.Collectors.toList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    @Transactional
    public SectionResponse createSection(AuthUser authUser, Long gameId, SectionCreateRequest request) {
        checkAdmin(authUser);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 경기 정보가 없습니다."));

        boolean exists = sectionRepository.existsByGameIdAndNameAndDeletedAtIsNull(gameId, request.getName());
        if (exists) {
            throw new CustomException(HttpStatus.CONFLICT, "같은 이름의 구역이 이미 존재합니다.");
        }

        Section section = SectionMapper.toEntity(game, request);
        section = sectionRepository.save(section);
        return SectionMapper.toResponse(section);
    }

    @Override
    @Transactional
    public SectionResponse updateSection(AuthUser authUser, Long sectionId, SectionUpdateRequest request) {
        checkAdmin(authUser);

        Section section = getActiveSection(sectionId);
        section.update(request.getName(), request.getPrice(), request.getSeatType());
        return SectionMapper.toResponse(section);
    }

    @Override
    @Transactional
    public void deleteSection(AuthUser authUser, Long sectionId) {
        checkAdmin(authUser);

        Section section = getActiveSection(sectionId);
        section.delete();
    }

    @Override
    public List<SectionResponse> getSectionsByGameId(Long gameId) {
        return sectionRepository.findByGameIdAndDeletedAtIsNull(gameId).stream()
                .map(SectionMapper::toResponse)
                .toList();
    }

    @Override
    public SectionResponse getSection(Long sectionId) {
        return SectionMapper.toResponse(getActiveSection(sectionId));
    }

    private Section getActiveSection(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 Section이 존재하지 않거나 삭제되었습니다."));
    }

    private void checkAdmin(AuthUser authUser) {
        if (!authUser.getUserRole().name().equals("ADMIN")) {
            throw new CustomException(HttpStatus.FORBIDDEN, "관리자만 수행할 수 있는 작업입니다.");
        }
    }
}