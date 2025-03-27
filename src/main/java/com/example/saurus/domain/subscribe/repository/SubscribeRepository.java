package com.example.saurus.domain.subscribe.repository;

import com.example.saurus.domain.subscribe.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    @Query("SELECT s FROM Subscribe s WHERE s.user.id = :userId " +
            "AND s.startDate <= :now AND s.endDate >= :now " +
            "ORDER BY s.startDate DESC")
    List<Subscribe> findActiveSubscriptions(@Param("userId") Long userId, @Param("now") LocalDateTime now);

}
