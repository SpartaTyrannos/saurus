package com.example.saurus.domain.auth.service;

import com.example.saurus.config.JwtUtil;
import com.example.saurus.domain.auth.dto.request.SigninRequestDto;
import com.example.saurus.domain.auth.dto.request.SignupRequestDto;
import com.example.saurus.domain.auth.dto.response.SigninResponseDto;
import com.example.saurus.domain.auth.dto.response.SignupResponseDto;
import com.example.saurus.domain.auth.entity.RefreshToken;
import com.example.saurus.domain.auth.repository.RefreshTokenRepository;
import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.user.entity.User;
import com.example.saurus.domain.user.enums.UserRole;
import com.example.saurus.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

       UserRole userRole = UserRole.ROLE_USER;

        User newUser = new User(
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getName(),
                requestDto.getPhone(),
                userRole
        );

        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhone(),
                savedUser.getUserRole()
        );

        return new SignupResponseDto(
                bearerToken,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhone(),
                savedUser.getUserRole().name()
        );
    }

    @Transactional(readOnly = true)
    public SigninResponseDto signin(SigninRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST, "가입되지 않은 유저입니다.")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다.");
        }

        String accessToken = jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getUserRole()
        );
        String refreshToken = jwtUtil.createRefreshToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getUserRole());

        // 리프레시 토큰 DB 저장 또는 갱신 (Rotation 전략)
        refreshTokenRepository.findById(user.getId())
                .ifPresentOrElse(
                        existing -> existing.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken))
                );

        return new SigninResponseDto(accessToken, refreshToken);
    }
}
