package com.moa.global.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.global.auth.model.JwtUser;
import com.moa.global.auth.model.SecurityUser;
import com.moa.global.auth.utils.JwtService;
import com.moa.global.exception.custom.EntityNotFoundException;
import com.moa.global.filter.exception.BusinessAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.moa.global.exception.custom.ErrorCode.JWT_NOT_VALID;
import static com.moa.global.exception.custom.ErrorCode.USER_NOT_FOUND;

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
        if (jwtService.isTokenValid(accessToken)) {
            saveAuthentication(accessToken);
        } else {
            checkRefreshToken(request, response, accessToken);
        }
        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(String accessToken) {
        String email = jwtService.extractUserEmail(accessToken);
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(email));
    }

    private UsernamePasswordAuthenticationToken createAuthentication(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(new JwtUser(user.getId()), null, securityUser.getAuthorities());
    }

    private void checkRefreshToken(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        String refreshToken = jwtService.extractToken(request, "AuthorizationRefresh")
                .filter(jwtService::isTokenValid)
                .orElseThrow(() -> new BusinessAuthenticationException(JWT_NOT_VALID));

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessAuthenticationException(JWT_NOT_VALID));
        validateEmail(accessToken, user);
        String recreateAccessToken = jwtService.createAccessToken(user.getEmail());
        jwtService.setAccessTokenInHeader(response, recreateAccessToken);
        saveAuthentication(recreateAccessToken);
    }

    private void validateEmail(String accessToken, User user) {
        try {
            String email = jwtService.extractUserEmail(accessToken);
            if (!email.equals(user.getEmail())) {
                throw new BusinessAuthenticationException(JWT_NOT_VALID);
            }
        } catch (JWTDecodeException exception) {
            throw new BusinessAuthenticationException(JWT_NOT_VALID);
        }
    }
}