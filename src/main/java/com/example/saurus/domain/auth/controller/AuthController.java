package com.example.saurus.domain.auth.controller;

import com.example.saurus.config.JwtUtil;
import com.example.saurus.domain.auth.dto.request.SigninRequestDto;
import com.example.saurus.domain.auth.dto.request.SignupRequestDto;
import com.example.saurus.domain.auth.dto.response.SigninResponseDto;
import com.example.saurus.domain.auth.dto.response.SignupResponseDto;
import com.example.saurus.domain.auth.entity.RefreshToken;
import com.example.saurus.domain.auth.repository.RefreshTokenRepository;
import com.example.saurus.domain.auth.service.AuthService;
import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.user.entity.User;
import com.example.saurus.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auths")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponseDto> signin(@Valid @RequestBody SigninRequestDto signinRequest) {
        return ResponseEntity.ok(authService.signin(signinRequest));
    }

    // Refresh 엔드포인트: Authorization 헤더로 refresh token 전달
    @PostMapping("/refresh")
    public ResponseEntity<SigninResponseDto> refreshToken(
            @RequestHeader("Authorization") String bearerToken) {

        String token = jwtUtil.substringToken(bearerToken);
        Claims claims;

        try {
            claims = jwtUtil.extractClaims(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Refresh Token 만료. 다시 로그인 필요");
        }


        String tokenType = claims.get("tokenType", String.class);
        if (!"refresh".equalsIgnoreCase(tokenType)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Access Token이 제공되었습니다. Refresh Token 필요");
        }

        Long userId = Long.parseLong(claims.getSubject());

        RefreshToken saved = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "Refresh Token 없음"));

        if (!saved.getToken().equals(bearerToken)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Refresh Token 불일치");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "유저 없음"));

        String newAccessToken = jwtUtil.createToken(user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getUserRole()
        );
        String newRefreshToken = jwtUtil.createRefreshToken(user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getUserRole()
        );
        saved.updateToken(newRefreshToken);

        return ResponseEntity.ok(new SigninResponseDto(newAccessToken, newRefreshToken));
    }
}

