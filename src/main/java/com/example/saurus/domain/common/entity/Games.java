package com.example.saurus.domain.common.entity;

import com.example.saurus.domain.common.enums.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "games")
public class Games extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String place;

    @Nullable
    private String description;

    @Enumerated(EnumType.STRING)
    private Team opponent;

    private LocalDateTime gameTime;

    private LocalDateTime ticketOpen;
}
