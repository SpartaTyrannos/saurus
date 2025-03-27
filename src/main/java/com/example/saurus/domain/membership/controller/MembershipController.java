package com.example.saurus.domain.membership.controller;

import com.example.saurus.domain.common.annotation.Admin;
import com.example.saurus.domain.membership.dto.request.MembershipRequestDto;
import com.example.saurus.domain.membership.dto.response.MembershipResponseDto;
import com.example.saurus.domain.membership.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @Admin
    @PostMapping
    public ResponseEntity<String> saveMembership(
            @Valid @RequestBody MembershipRequestDto request
    ) {
        return ResponseEntity.ok(membershipService.saveMembership(request));
    }

    @GetMapping
    public ResponseEntity<Page<MembershipResponseDto>> getMemberships(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(membershipService.getMemberships(page, size));
    }

    @GetMapping("/{membershipId}")
    public ResponseEntity<MembershipResponseDto> getMembership(
            @PathVariable Long membershipId
    ) {
        return ResponseEntity.ok(membershipService.getMembership(membershipId));
    }

    @Admin
    @PutMapping("/{membershipId}")
    public ResponseEntity<String> updateMembership(
            @PathVariable Long membershipId,
            @Valid @RequestBody MembershipRequestDto request
    ) {
        return ResponseEntity.ok(membershipService.updateMembership(membershipId, request));
    }
}
