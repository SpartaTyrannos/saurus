package com.example.saurus.domain.common.aop;

import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.user.enums.UserRole;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import com.example.saurus.domain.common.dto.AuthUser;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserAccessAspect {

    @Before("@annotation(com.example.saurus.domain.common.annotation.User)")
    public void userApiAccess(JoinPoint joinPoint) {
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserRole role = authUser.getUserRole();

        if (role != UserRole.ROLE_USER) {
            throw new CustomException(HttpStatus.FORBIDDEN, "일반 사용자 권한이 필요합니다.");
        }
    }
}
