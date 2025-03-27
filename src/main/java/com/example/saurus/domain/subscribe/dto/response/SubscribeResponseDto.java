package com.example.saurus.domain.subscribe.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SubscribeResponseDto {

    private final Long id;
    private final Long membershipId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final boolean active;

    public SubscribeResponseDto(Long id, Long membershipId, LocalDateTime startDate, LocalDateTime endDate, boolean active) {
        this.id = id;
        this.membershipId = membershipId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }
}
