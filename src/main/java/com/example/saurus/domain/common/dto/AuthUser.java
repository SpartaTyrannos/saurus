package com.example.saurus.domain.common.dto;

import com.example.saurus.domain.user.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

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

    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.getAuthority()));
    }
}
