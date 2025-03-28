package com.example.saurus.config;

import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String refreshTokenHeader = request.getHeader("Refresh-Token");


        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwt = jwtUtil.substringToken(authorizationHeader);
                Claims claims = jwtUtil.extractClaims(jwt);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(claims);
                }
            }
            // ✅ AccessToken 없고 RefreshToken만 있는 경우
            else if (authorizationHeader == null &&
                    refreshTokenHeader != null && refreshTokenHeader.startsWith("Bearer ")) {

                String refreshToken = jwtUtil.substringToken(refreshTokenHeader);
                Claims claims = jwtUtil.extractClaims(refreshToken); // 여기서 예외나면 catch에서 처리됨

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(claims);
                }
            }
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
            return;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
            return;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
            return;
        } catch (Exception e) {
            log.error("Internal server error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        chain.doFilter(request, response);
    }

    private void setAuthentication(Claims claims) {
        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        String name = claims.get("name", String.class);
        String phone = claims.get("phone", String.class);
        UserRole userRole = UserRole.of(claims.get("userRole", String.class));

        AuthUser authUser = new AuthUser(userId, email, name, phone, userRole);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
