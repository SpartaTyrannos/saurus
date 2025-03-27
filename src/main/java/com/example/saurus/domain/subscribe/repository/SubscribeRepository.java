package com.example.saurus.domain.subscribe.repository;

import com.example.saurus.domain.subscribe.entity.Subscribe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    @EntityGraph(attributePaths = {"user", "membership"})
    boolean existsByUserIdAndMembershipId(Long userId, Long membershipId);

    @EntityGraph(attributePaths = {"user", "membership"})
    Page<Subscribe> findAllByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Subscribe> findWithUserById(Long id);

    @Query("SELECT s FROM Subscribe s WHERE s.user.id = :userId " +
            "AND s.startDate <= :now AND s.endDate >= :now " +
            "ORDER BY s.startDate DESC")
    List<Subscribe> findActiveSubscriptions(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
