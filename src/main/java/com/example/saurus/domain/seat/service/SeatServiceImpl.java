package com.example.saurus.domain.seat.service;

import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
import com.example.saurus.domain.seat.dto.request.SeatUpdateRequest;
import com.example.saurus.domain.seat.dto.response.SeatResponse;
import com.example.saurus.domain.seat.entity.Seat;
import com.example.saurus.domain.seat.mapper.SeatMapper;
import com.example.saurus.domain.seat.repository.SeatRepository;
import com.example.saurus.domain.section.entity.Section;
import com.example.saurus.domain.section.repository.SectionRepository;
import jakarta.persistence.EntityNotFoundException;
import static java.util.stream.Collectors.toList;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SectionRepository sectionRepository;

    @Override
    @Transactional
    public SeatResponse createSeat(Long sectionId, SeatCreateRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("해당 Section이 존재하지 않거나 삭제되었습니다."));

        if (seatRepository.existsBySectionIdAndSeatRowAndNumberAndDeletedAtIsNull(
                sectionId, request.getSeatRow(), request.getNumber().toString())) {
            throw new IllegalArgumentException("해당 좌석은 이미 존재합니다.");
        }

        Seat seat = SeatMapper.toEntity(section, request);
        seat.setSection(section); // 연관관계 설정
        seat = seatRepository.save(seat);
        return SeatMapper.toResponse(seat);
    }

    @Override
    @Transactional
    public SeatResponse updateSeat(Long seatId, SeatUpdateRequest request) {
        Seat seat = getActiveSeat(seatId);
        seat.update(request.getRow(), request.getNumber().toString(), request.getSeatType());
        return SeatMapper.toResponse(seat);
    }

    @Override
    @Transactional
    public void deleteSeat(Long seatId) {
        Seat seat = getActiveSeat(seatId);
        seat.delete();
    }

    @Override
    public List<SeatResponse> getSeatsBySectionId(Long sectionId) {
        return seatRepository.findBySectionIdAndDeletedAtIsNull(sectionId).stream()
                .map(SeatMapper::toResponse)
                .collect(toList());
    }

    @Override
    public SeatResponse getSeat(Long seatId) {
        return SeatMapper.toResponse(getActiveSeat(seatId));
    }

    private Seat getActiveSeat(Long seatId) {
        return seatRepository.findByIdAndDeletedAtIsNull(seatId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Seat이 존재하지 않거나 삭제되었습니다."));
    }
}