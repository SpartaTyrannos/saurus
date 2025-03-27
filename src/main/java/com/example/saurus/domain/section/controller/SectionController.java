package com.example.saurus.domain.section.controller;

import com.example.saurus.domain.common.annotation.Admin;
import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.section.dto.request.SectionCreateRequest;
import com.example.saurus.domain.section.dto.request.SectionUpdateRequest;
import com.example.saurus.domain.section.dto.response.SectionResponse;
import com.example.saurus.domain.section.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games/{gameId}/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @Admin
    @PostMapping
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long gameId,
            @RequestBody @Valid SectionCreateRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(sectionService.createSection(authUser, gameId, request));
    }

    @Admin
    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long gameId,
            @PathVariable Long sectionId,
            @RequestBody @Valid SectionUpdateRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(sectionService.updateSection(authUser, gameId, sectionId, request));
    }

    @Admin
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(

            @PathVariable Long gameId,
            @PathVariable Long sectionId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        sectionService.deleteSection(authUser, gameId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<SectionResponse>> getSections(
            @PathVariable Long gameId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(sectionService.getSectionsByGameId(gameId, pageable));
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> getSection(
            @PathVariable Long gameId,
            @PathVariable Long sectionId
    ) {
        return ResponseEntity.ok(sectionService.getSection(gameId, sectionId));
    }
}