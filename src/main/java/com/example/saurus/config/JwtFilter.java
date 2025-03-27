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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String url = request.getRequestURI();

        if (url.startsWith("/api/v1/auths")) {
            chain.doFilter(request, response);
            return;
        }

        String bearerJwt = request.getHeader("Authorization");

        if (bearerJwt == null) {
            // 토큰이 없는 경우 400을 반환합니다.
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
            return;
        }

        String jwt = jwtUtil.substringToken(bearerJwt);

        try {
            // JWT 유효성 검사와 claims 추출
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            String subject = claims.getSubject();
            log.info("Parsed subject from JWT: {}", subject);
            if (subject == null) {
                throw new IllegalArgumentException("JWT subject (userId) 누락됨");
            }
            Long userId = Long.parseLong(subject);
            String email = claims.get("email", String.class);
            String name = claims.get("name", String.class);
            String phone = claims.get("phone", String.class);
            String roleString = claims.get("userRole", String.class);
            UserRole userRole = UserRole.of(roleString);

            AuthUser authUser = new AuthUser(userId, email, name, phone, userRole);
            // SecurityContext에 인증 객체 등록
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                    authUser,
                    List.of(new SimpleGrantedAuthority(userRole.getAuthority()))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("userId", Long.parseLong(claims.getSubject()));
            log.info("JwtFilter set userId: {}", userId);

            request.setAttribute("email", claims.get("email"));
            request.setAttribute("name", claims.get("name"));
            request.setAttribute("phone", claims.get("phone"));
            request.setAttribute("userRole", claims.get("userRole"));

            chain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("Exception during JWT processing: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않는 JWT 토큰입니다.");
        }
    }
}
