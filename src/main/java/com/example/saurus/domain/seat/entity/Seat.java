package com.example.saurus.domain.seat.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.section.entity.Section;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    // DB에서 Unique 값 설정
    @Column(length = 10)
    private String row;

    // DB에서 Unique 값 설정
    @Column(length = 20)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    public void update(String row, String number, SeatType seatType) {
        this.row = row;
        this.number = number;
        this.seatType = seatType;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}