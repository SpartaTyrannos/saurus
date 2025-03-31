package com.example.saurus.domain.subscribe.entity;

import com.example.saurus.domain.common.entity.BaseEntity;
import com.example.saurus.domain.user.entity.User;
import com.example.saurus.domain.membership.entity.Membership;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Year;

@Getter
@Entity
@NoArgsConstructor
@Table( name = "subscribes" )
public class Subscribe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    public Subscribe(User user, Membership membership) {
        this.startDate = LocalDateTime.now();
        this.endDate = calculateEndDate();
        this.user = user;
        this.membership = membership;
    }

    private LocalDateTime calculateEndDate() {
        int currentYear = Year.now().getValue();
        return LocalDateTime.of(currentYear, 12, 31, 23, 59, 59);
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return (now.isAfter(startDate) || now.isEqual(startDate)) && (now.isBefore(endDate) || now.isEqual(endDate));
    }

    public void delete() {
        this.endDate = LocalDateTime.now();
    }
}
