package com.example.saurus.domain.user.service;

import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.user.dto.request.UserDeleteRequestDto;
import com.example.saurus.domain.user.dto.request.UserUpdateRequestDto;
import com.example.saurus.domain.user.dto.response.UserResponseDto;
import com.example.saurus.domain.user.entity.User;
import com.example.saurus.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {
        User user = findUserById(userId);
        return new UserResponseDto(user.getId(), user.getEmail(), user.getName(), user.getPhone(), user.getUserRole().toString(), user.getCreatedAt(), user.getModifiedAt());
    }

    public UserResponseDto updateProfile(Long userId, UserUpdateRequestDto requestDto) {
        User user = findUserById(userId);

        if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "기존 비밀번호와 일치하지 않습니다.");
        }

        user.changePassword(passwordEncoder.encode(requestDto.getNewPassword()));
        user.changeName(requestDto.getName());

        User updatedUser = userRepository.save(user);

        return new UserResponseDto(updatedUser.getId(), updatedUser.getEmail(), updatedUser.getName(), updatedUser.getPhone(), updatedUser.getUserRole().toString(), updatedUser.getCreatedAt(), updatedUser.getModifiedAt());
    }

    @Transactional
    public String deleteUser(Long userId, UserDeleteRequestDto requestDto) {
        User user = findUserById(userId);

        if (user.getDeletedAt() != null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 탈퇴한 사용자입니다.");
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        user.delete();

        return "회원 탈퇴가 완료되었습니다.";
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "유저를 찾을 수 없습니다."));
    }

}
