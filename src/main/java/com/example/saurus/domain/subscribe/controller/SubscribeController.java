package com.example.saurus.domain.subscribe.controller;

import com.example.saurus.domain.common.annotation.User;
import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.subscribe.dto.response.SubscribeResponseDto;
import com.example.saurus.domain.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    @User
    @PostMapping("/memberships/{membershipId}/subscribes")
    public ResponseEntity<String> saveSubscribe(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long membershipId
    ) {
        return ResponseEntity.ok(subscribeService.saveSubscribe(authUser, membershipId));
    }

    @User
    @GetMapping("/my/subscribes")
    public ResponseEntity<Page<SubscribeResponseDto>> getSubscribes(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(subscribeService.getSubscribes(authUser, page, size));
    }

    @User
    @DeleteMapping("/my/subscribes/{subscribeId}")
    public ResponseEntity<String> deleteSubscribe(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long subscribeId
    ) {
        return ResponseEntity.ok(subscribeService.deleteSubscribe(authUser, subscribeId));
    }
}
