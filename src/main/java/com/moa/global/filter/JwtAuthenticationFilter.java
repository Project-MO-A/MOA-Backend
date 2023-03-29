package com.moa.global.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.global.auth.model.Claims;
import com.moa.global.auth.model.JwtUser;
import com.moa.global.auth.model.SecurityUser;
import com.moa.global.auth.utils.JwtService;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.global.exception.auth.BusinessAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.moa.global.exception.ErrorCode.JWT_NOT_VALID;
import static com.moa.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * accessToken 유효 -> authentication 저장
     * accessToken 만료
     * refreshToken 유효 -> authentication 저장, accessToken 갱신
     * refreshToken 만료 -> authentication 저장 X
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> accessToken = jwtService.extractToken(request, "Authorization");
        if (accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        doFilterInternal(request, response, filterChain, accessToken.get());
    }

    private void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String accessToken) throws ServletException, IOException {
        if (!jwtService.isTokenValid(accessToken)) {
            checkRefreshToken(request, response, accessToken);
        }
        saveAuthentication(accessToken);
        filterChain.doFilter(request, response);
    }

    private void checkRefreshToken(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        String refreshToken = jwtService.extractToken(request, "AuthorizationRefresh")
                .filter(jwtService::isTokenValid)
                .orElseThrow(() -> new BusinessAuthenticationException(JWT_NOT_VALID));

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        validateEmail(accessToken, user);
        String recreateAccessToken = jwtService.createAccessToken(user.getId(), (List<GrantedAuthority>) new SecurityUser(user).getAuthorities());
        jwtService.setAccessTokenInHeader(response, recreateAccessToken);
    }

    private void validateEmail(String accessToken, User user) {
        try {
            Long userId = jwtService.extractClaims(accessToken).userId();
            if (userId.equals(user.getId())) {
                throw new BusinessAuthenticationException(JWT_NOT_VALID);
            }
        } catch (JWTDecodeException exception) {
            throw new BusinessAuthenticationException(JWT_NOT_VALID);
        }
    }

    private void saveAuthentication(String accessToken) {
        Claims claims = jwtService.extractClaims(accessToken);
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(claims));
    }

    private UsernamePasswordAuthenticationToken createAuthentication(Claims claims) {
        return new UsernamePasswordAuthenticationToken(new JwtUser(claims.userId()), null, convertRole(claims.role()));
    }

    private Collection<? extends GrantedAuthority> convertRole(List<String> role) {
        return role.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}