package com.example.saurus.domain.seat.repository;

import com.example.saurus.domain.seat.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    //섹션 내 좌석 전체 조회
    Page<Seat> findBySectionIdAndDeletedAtIsNull(Long sectionId, Pageable pageable);

    // 소프트 딜리트 필터링 포함 단건 조회
    Optional<Seat> findByIdAndDeletedAtIsNull(Long seatId);

    //좌석 중복 방지 체크
    boolean existsBySectionIdAndSeatRowAndNumberAndDeletedAtIsNull(Long sectionId, String seatRow, String number);
}

