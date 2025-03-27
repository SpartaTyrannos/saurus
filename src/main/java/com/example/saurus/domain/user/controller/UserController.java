package com.example.saurus.domain.user.controller;

import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.user.dto.request.UserDeleteRequestDto;
import com.example.saurus.domain.user.dto.request.UserUpdateRequestDto;
import com.example.saurus.domain.user.dto.response.UserResponseDto;
import com.example.saurus.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/my")
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(userService.getUser(authUser.getId()));
    }

    // 내 정보 수정
    @PutMapping("/my")
    public ResponseEntity<UserResponseDto> updateProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserUpdateRequestDto userUpdateRequest
    ) {
        return ResponseEntity.ok(userService.updateProfile(authUser.getId(), userUpdateRequest));
    }

    // 회원 탈퇴
    @PostMapping("/my")
    public ResponseEntity<String> deleteUser(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserDeleteRequestDto userDeleteRequest
    ) {
        return ResponseEntity.ok(userService.deleteUser(authUser.getId(), userDeleteRequest));
    }

}
