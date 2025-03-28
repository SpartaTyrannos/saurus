package com.example.saurus.domain.seat.service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SectionRepository sectionRepository;

    @Override
    @Transactional
    public SeatResponse createSeat(AuthUser authUser, Long gameId, Long sectionId, SeatCreateRequest request) {
        Section section = getSectionWithCheck(gameId, sectionId);

        if (!request.getSeatType().equals(section.getType())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 구역에는 동일한 타입의 좌석만 등록할 수 있습니다.");
        }

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
        validateSeatInGame(gameId, seat);

        if (!request.getSeatType().equals(seat.getSection().getType())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 구역에는 동일한 타입의 좌석만 설정할 수 있습니다.");
        }

        seat.update(request.getSeatRow(), request.getNumber().toString(), request.getSeatType());
        return SeatMapper.toResponse(seat);
    }

    @Override
    @Transactional
    public void deleteSeat(AuthUser authUser, Long gameId, Long seatId) {
        Seat seat = getActiveSeat(seatId);
        validateSeatInGame(gameId, seat);
        seat.delete();
    }

    @Override
    @Transactional
    public void deleteSeatsBySection(Section section) {
        List<Seat> seats = seatRepository.findBySectionIdAndDeletedAtIsNull(section.getId());
        for (Seat seat : seats) {
            seat.delete(); // Soft delete
        }
    }

    @Override
    public Page<SeatResponse> getSeatsBySectionId(Long gameId, Long sectionId, Pageable pageable) {
        Section section = getSectionWithCheck(gameId, sectionId);
        return seatRepository.findBySectionIdAndDeletedAtIsNull(sectionId, pageable)
                .map(SeatMapper::toResponse);
    }

    @Override
    public SeatResponse getSeat(Long gameId, Long seatId) {
        Seat seat = getActiveSeat(seatId);
        validateSeatInGame(gameId, seat);
        return SeatMapper.toResponse(seat);
    }

    @Override
    @Transactional
    public void createSeatsForSection(Section section) {
        List<Seat> seats = new ArrayList<>();
        for (char row = 'A'; row <= 'Z'; row++) {
            for (int number = 1; number <= 10; number++) {
                seats.add(Seat.builder()
                        .section(section)
                        .seatRow(String.valueOf(row))
                        .number(String.valueOf(number))
                        .seatType(section.getType())
                        .build());
            }
        }
        seatRepository.saveAll(seats);
    }

    @Override
    public Optional<Seat> findByIdAndDeletedAtIsNull(Long seatId) {
        return seatRepository.findByIdAndDeletedAtIsNull(seatId);
    }

    private Seat getActiveSeat(Long seatId) {
        return seatRepository.findByIdAndDeletedAtIsNull(seatId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 좌석이 존재하지 않거나 삭제되었습니다."));
    }

    private Section getSectionWithCheck(Long gameId, Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 구역이 존재하지 않거나 삭제되었습니다."));

        if (!section.getGame().getId().equals(gameId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 구역은 해당 경기에 속하지 않습니다.");
        }
        return section;
    }

    private void validateSeatInGame(Long gameId, Seat seat) {
        if (!seat.getSection().getGame().getId().equals(gameId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 좌석은 지정된 경기에 속하지 않습니다.");
        }
    }

    @Override
    public boolean existsBySectionIdAndSeatRowAndNumberAndDeletedAtIsNull(Long sectionId, String seatRow, String number) {
        return seatRepository.existsBySectionIdAndSeatRowAndNumberAndDeletedAtIsNull(sectionId, seatRow, number);
    }
}
