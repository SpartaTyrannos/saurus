package com.example.saurus.domain.seat.service;

import com.example.saurus.domain.common.annotation.Admin;
import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.seat.dto.request.SeatCreateRequest;
import com.example.saurus.domain.seat.dto.request.SeatUpdateRequest;
import com.example.saurus.domain.seat.dto.response.SeatResponse;
import com.example.saurus.domain.seat.entity.Seat;
import com.example.saurus.domain.seat.mapper.SeatMapper;
import com.example.saurus.domain.seat.repository.SeatRepository;
import com.example.saurus.domain.section.entity.Section;
import com.example.saurus.domain.section.repository.SectionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SectionRepository sectionRepository;

    @Override
    @Transactional
    public SeatResponse createSeat(AuthUser authUser, Long gameId, Long sectionId, SeatCreateRequest request) {

        Section section = sectionRepository.findById(sectionId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 구역이 존재하지 않거나 삭제되었습니다."));

        boolean exists = seatRepository.existsBySectionIdAndSeatRowAndNumberAndDeletedAtIsNull(
                sectionId, request.getSeatRow(), request.getNumber().toString()
        );
        if (exists) {
            throw new CustomException(HttpStatus.CONFLICT, "해당 좌석은 이미 존재합니다.");
        }

        Seat seat = SeatMapper.toEntity(section, request);
        seat = seatRepository.save(seat);
        return SeatMapper.toResponse(seat);
    }

    @Override
    @Transactional
    public SeatResponse updateSeat(AuthUser authUser, Long gameId, Long seatId, SeatUpdateRequest request) {

        Seat seat = getActiveSeat(seatId);
        seat.update(request.getSeatRow(), request.getNumber().toString(), request.getSeatType());
        return SeatMapper.toResponse(seat);
    }

    @Override
    @Transactional
    public void deleteSeat(AuthUser authUser, Long gameId, Long seatId) {

        Seat seat = getActiveSeat(seatId);
        seat.delete();
    }

    @Override
    public Page<SeatResponse> getSeatsBySectionId(Long gameId, Long sectionId, Pageable pageable) {
        Section section = sectionRepository.findById(sectionId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 구역입니다."));

        validateSectionBelongsToGame(gameId, section);

        return seatRepository.findBySectionIdAndDeletedAtIsNull(sectionId, pageable)
                .map(SeatMapper::toResponse);
    }

    @Override
    public SeatResponse getSeat(Long gameId, Long seatId) {
        Seat seat = getActiveSeat(seatId);
        validateSectionBelongsToGame(gameId, seat.getSection());
        return SeatMapper.toResponse(seat);
    }

    private Seat getActiveSeat(Long seatId) {
        return seatRepository.findByIdAndDeletedAtIsNull(seatId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 좌석이 존재하지 않거나 삭제되었습니다."));
    }

    private void validateSectionBelongsToGame(Long gameId, Section section) {
        if (!section.getGame().getId().equals(gameId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 구역은 해당 경기에 속하지 않습니다.");
        }
    }
}
