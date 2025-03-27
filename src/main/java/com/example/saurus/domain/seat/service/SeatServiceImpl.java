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
import jakarta.persistence.EntityNotFoundException;
import static java.util.stream.Collectors.toList;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public SeatResponse createSeat(AuthUser authUser, Long sectionId, SeatCreateRequest request) {
        checkAdmin(authUser);

        Section section = sectionRepository.findById(sectionId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 Section이 존재하지 않거나 삭제되었습니다."));

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
    public SeatResponse updateSeat(AuthUser authUser, Long seatId, SeatUpdateRequest request) {
        checkAdmin(authUser);

        Seat seat = getActiveSeat(seatId);
        seat.update(request.getSeatRow(), request.getNumber().toString(), request.getSeatType());
        return SeatMapper.toResponse(seat);
    }

    @Override
    @Transactional
    public void deleteSeat(AuthUser authUser, Long seatId) {
        checkAdmin(authUser);

        Seat seat = getActiveSeat(seatId);
        seat.delete();
    }

    @Override
    public List<SeatResponse> getSeatsBySectionId(Long sectionId) {
        return seatRepository.findBySectionIdAndDeletedAtIsNull(sectionId).stream()
                .map(SeatMapper::toResponse)
                .toList();
    }

    @Override
    public SeatResponse getSeat(Long seatId) {
        return SeatMapper.toResponse(getActiveSeat(seatId));
    }

    private Seat getActiveSeat(Long seatId) {
        return seatRepository.findByIdAndDeletedAtIsNull(seatId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 Seat이 존재하지 않거나 삭제되었습니다."));
    }

    private void checkAdmin(AuthUser authUser) {
        if (!authUser.getUserRole().name().equals("ADMIN")) {
            throw new CustomException(HttpStatus.FORBIDDEN, "관리자만 수행할 수 있는 작업입니다.");
        }
    }
}
