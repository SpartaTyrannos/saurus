package com.example.saurus.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class SignupResponseDto {

    private final String bearerToken;

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String userRole;

    public SignupResponseDto(String bearerToken, Long id, String email, String name, String phone, String userRole) {
        this.bearerToken = bearerToken;
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userRole = userRole;
    }
}
