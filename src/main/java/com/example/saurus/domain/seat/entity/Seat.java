package com.example.saurus.domain.seat.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.seat.enums.SeatType;
import com.example.saurus.domain.section.entity.Section;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"section_id", "seat_row", "number"})})
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

    @Column(name = "seat_row", length = 10, nullable = false)
    private String seatRow;

    @Column(length = 20, nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    public void update(String seatrow, String number, SeatType seatType) {
        this.seatRow = seatrow;
        this.number = number;
        this.seatType = seatType;
    }
}
