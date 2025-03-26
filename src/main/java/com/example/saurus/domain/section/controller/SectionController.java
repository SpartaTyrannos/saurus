package com.example.saurus.domain.section.controller;

import com.example.saurus.domain.section.dto.request.SectionCreateRequest;
import com.example.saurus.domain.section.dto.request.SectionUpdateRequest;
import com.example.saurus.domain.section.dto.response.SectionResponse;
import com.example.saurus.domain.section.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games/{gameId}/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long gameId,
            @RequestBody @Valid SectionCreateRequest request
    ) {
        SectionResponse response = sectionService.createSection(gameId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long sectionId,
            @RequestBody @Valid SectionUpdateRequest request
    ) {
        SectionResponse response = sectionService.updateSection(sectionId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long sectionId) {
        sectionService.deleteSection(sectionId);
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