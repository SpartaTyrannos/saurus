package com.example.saurus.domain.section.service;

import com.example.saurus.domain.game.entity.Game;
import com.example.saurus.domain.game.repository.GameRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional
    public SectionResponse createSection(Long gameId, SectionCreateRequest request) {
        log.debug("gameId: {}", gameId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("해당 경기 정보가 없습니다."));

        if (sectionRepository.existsByGameIdAndNameAndDeletedAtIsNull(gameId, request.getName())) {
            throw new IllegalArgumentException("같은 이름의 구역이 이미 존재합니다.");
        }

        Section section = SectionMapper.toEntity(game, request);
        section = sectionRepository.save(section); // 이미 game이 설정되어 있으므로 setGame 필요 없음

        return SectionMapper.toResponse(section);
    }

    @Override
    @Transactional
    public SectionResponse updateSection(Long sectionId, SectionUpdateRequest request) {
        Section section = getActiveSection(sectionId);
        section.update(request.getName(), request.getPrice(), request.getSeatType());
        return SectionMapper.toResponse(section);
    }

    @Override
    @Transactional
    public void deleteSection(Long sectionId) {
        Section section = getActiveSection(sectionId);
        section.delete();
    }

    @Override
    public List<SectionResponse> getSectionsByGameId(Long gameId) {
        return sectionRepository.findByGameIdAndDeletedAtIsNull(gameId).stream()
                .map(SectionMapper::toResponse)
                .collect(toList());
    }

    @Override
    public SectionResponse getSection(Long sectionId) {
        return SectionMapper.toResponse(getActiveSection(sectionId));
    }

    private Section getActiveSection(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("해당 Section이 존재하지 않거나 삭제되었습니다."));
    }
}