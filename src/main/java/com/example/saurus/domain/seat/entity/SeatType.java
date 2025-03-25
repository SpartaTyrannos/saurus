package com.example.saurus.domain.seat.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SeatType {

    VIP("중앙석 (프리미엄/VIP)"),
    TABLE("테이블석"),
    EXCITING("익사이팅존"),
    BLUE("블루지정석"),
    ORANGE("오렌지석"),
    RED("레드석"),
    NAVY("네이비석"),
    OUT("외야지정석");

    private final String label;

    SeatType(String label) {
        this.label = label;
    }

    @JsonValue
    public String toJson() {
        return label;
    }
}