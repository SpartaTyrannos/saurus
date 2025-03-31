package com.example.saurus.domain.user.entity;

import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table( name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    public User(String email, String password, String name, String phone, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.userRole = userRole;
    }

    private User(Long id, String email, String name, String phone, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userRole = userRole;
    }

    public static User fromAuthUser(AuthUser authUser) {
        return new User(authUser.getId(), authUser.getEmail(), authUser.getName(), authUser.getPhone(), authUser.getUserRole());
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
