package com.example.saurus.config;

import com.example.saurus.domain.common.annotation.Admin;
import com.example.saurus.domain.common.annotation.User;
import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.user.enums.UserRole;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {
// 논의 필요
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthUser.class); // 수정
    }

    @Override
    public Object resolveArgument(
            @Nullable MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        // SecurityContext에서 인증 객체를 꺼냄
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUser)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "유저 인증 정보가 없습니다.");
        }

        return (AuthUser) authentication.getPrincipal();
    }

}
