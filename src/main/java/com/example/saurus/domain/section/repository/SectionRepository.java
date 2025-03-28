package com.example.saurus.domain.section.repository;

import com.example.saurus.domain.section.entity.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    // 특정 경기의 삭제되지 않은 구역 목록 조회
    Page<Section> findByGameIdAndDeletedAtIsNull(Long gameId, Pageable pageable);

    // 같은 이름의 구역 중복 체크
    boolean existsByGameIdAndNameAndDeletedAtIsNull(Long gameId, String name);

    // 전체 목록 조회용 (삭제되지 않은 섹션들)
    List<Section> findByGameIdAndDeletedAtIsNull(Long gameId);

}