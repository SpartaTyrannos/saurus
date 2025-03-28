package com.example.saurus.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    private Long userId;

    @Column(nullable = false)
    private String token;

    public RefreshToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public void updateToken(String token) {
        this.token = token;
    }

}
