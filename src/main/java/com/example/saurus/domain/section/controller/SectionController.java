package com.example.saurus.domain.section.controller;

import com.example.saurus.domain.common.annotation.Auth;
import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.section.dto.request.SectionCreateRequest;
import com.example.saurus.domain.section.dto.request.SectionUpdateRequest;
import com.example.saurus.domain.section.dto.response.SectionResponse;
import com.example.saurus.domain.section.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games/{gameId}/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long gameId,
            @RequestBody @Valid SectionCreateRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(sectionService.createSection(authUser, gameId, request));
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long sectionId,
            @RequestBody @Valid SectionUpdateRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(sectionService.updateSection(authUser, sectionId, request));
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long sectionId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        sectionService.deleteSection(authUser, sectionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SectionResponse>> getSections(@PathVariable Long gameId) {
        return ResponseEntity.ok(sectionService.getSectionsByGameId(gameId));
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> getSection(@PathVariable Long sectionId) {
        return ResponseEntity.ok(sectionService.getSection(sectionId));
    }
}