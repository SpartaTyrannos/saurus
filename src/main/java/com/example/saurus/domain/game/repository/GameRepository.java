package com.example.saurus.domain.game.repository;

import com.example.saurus.domain.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g  WHERE g.gameTime > :startDate AND g.gameTime < :endDate ORDER BY g.gameTime")
    Page<Game> findAllByDate(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT g FROM Game g WHERE g.title LIKE %:title% AND g.gameTime > :startDate AND g.gameTime < :endDate ORDER BY g.gameTime")
    Page<Game> findAllByTitleAndDate(Pageable pageable, String title, LocalDateTime startDate, LocalDateTime endDate);
}
