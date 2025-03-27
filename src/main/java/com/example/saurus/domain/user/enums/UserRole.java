package com.example.saurus.domain.user.enums;

import java.util.Arrays;

public enum UserRole {
    ROLE_USER(Authority.USER),
    ROLE_ADMIN(Authority.ADMIN);

    private final String userRole;

    UserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getAuthority() {
        return userRole;
    }

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 UserRole"));
    }
}
