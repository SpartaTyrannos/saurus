package com.example.saurus.domain.section.mapper;

import com.example.saurus.domain.game.entity.Game;
import com.example.saurus.domain.section.dto.request.SectionCreateRequest;
import com.example.saurus.domain.section.dto.request.SectionUpdateRequest;
import com.example.saurus.domain.section.dto.response.SectionResponse;
import com.example.saurus.domain.section.entity.Section;

public class SectionMapper {

    public static Section toEntity(Game game, SectionCreateRequest request) {
        return Section.builder()
                .game(game)
                .name(request.getName())
                .price(request.getPrice())
                .type(request.getSeatType())
                .build();
    }

    public static void updateEntity(Section section, SectionUpdateRequest request) {
        section.update(
                request.getName(),
                request.getPrice(),
                request.getSeatType()
        );
    }

    public static SectionResponse toResponse(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .gameId(section.getGame().getId())
                .name(section.getName())
                .price(section.getPrice())
                .seatType(section.getType())
                .build();
    }
}