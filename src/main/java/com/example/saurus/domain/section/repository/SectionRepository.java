package com.example.saurus.domain.section.repository;

import com.example.saurus.domain.section.entity.Section;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    // 특정 경기의 삭제되지 않은 구역 목록 조회
    Page<Section> findByGameIdAndDeletedAtIsNull(Long gameId, Pageable pageable);

    // FORCE INDEX를 적용한 네이티브 쿼리
    @Query(value = "SELECT * FROM sections FORCE INDEX(idx_section_game_deleted_id) " +
            "WHERE game_id = :gameId AND deleted_at IS NULL ORDER BY id LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Section> findSectionsWithForceIndex(@Param("gameId") Long gameId,
                                             @Param("limit") int limit,
                                             @Param("offset") int offset);

    long countByGameIdAndDeletedAtIsNull(Long gameId);

    // 같은 이름의 구역 중복 체크
    boolean existsByGameIdAndNameAndDeletedAtIsNull(Long gameId, String name);

    // 전체 목록 조회용 (삭제되지 않은 섹션들)
    List<Section> findByGameIdAndDeletedAtIsNull(Long gameId);

    @EntityGraph(attributePaths = {"game"})
    Optional<Section> findWithGameById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Section s WHERE s.id = :sectionId")
    Optional<Section> findWithLockById(@Param("sectionId") Long sectionId);
}
