package com.example.saurus.domain.membership.dto.response;

import lombok.Getter;

@Getter
public class MembershipResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;
    private final Double discount;
    private final Integer year;

    public MembershipResponseDto(Long id, String name, Integer price, Double discount, Integer year) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.year = year;
    }
}
