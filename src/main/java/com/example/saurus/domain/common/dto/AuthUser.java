package com.example.saurus.domain.common.dto;

import com.example.saurus.domain.user.enums.UserRole;
import lombok.Getter;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final String name;
    private final String phone;
    private final UserRole userRole;

    public AuthUser(Long id, String email, String name, String phone, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userRole = userRole;
    }
}
