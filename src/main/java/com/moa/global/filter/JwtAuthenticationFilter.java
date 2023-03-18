package com.moa.global.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.global.auth.model.JwtUser;
import com.moa.global.auth.model.SecurityUser;
import com.moa.global.auth.utils.JwtService;
import com.moa.global.filter.exception.JwtTokenNotValidException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationFailureHandler failureHandler;

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
        try {
            if (jwtService.isTokenValid(accessToken)) {
                saveAuthentication(accessToken);
                return;
            }
            checkRefreshToken(request, response, accessToken);
        } catch (AuthenticationException exception) {
            failureHandler.onAuthenticationFailure(request, response, exception);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(String accessToken) {
        String email = jwtService.extractUserEmail(accessToken);
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(email));
    }

    private void checkRefreshToken(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        String refreshToken = jwtService.extractToken(request, "AuthorizationRefresh")
                .filter(jwtService::isTokenValid)
                .orElseThrow(() -> new JwtTokenNotValidException("토큰이 유효하지 않습니다."));

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtTokenNotValidException("토큰이 유효하지 않습니다."));
        validateEmail(accessToken, user);
        String recreateAccessToken = jwtService.createAccessToken(user.getEmail());
        jwtService.setAccessTokenInHeader(response, recreateAccessToken);
        saveAuthentication(recreateAccessToken);
    }

    private UsernamePasswordAuthenticationToken createAuthentication(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보를 찾을 수 없습니다"));
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(new JwtUser(user.getId()), null, securityUser.getAuthorities());
    }

    private void validateEmail(String accessToken, User user) {
        try {
            String email = jwtService.extractUserEmail(accessToken);
            if (!email.equals(user.getEmail())) {
                throw new JwtTokenNotValidException("토큰이 유효하지 않습니다.");
            }
        } catch (JWTDecodeException exception) {
            throw new JwtTokenNotValidException("토큰이 유효하지 않습니다.");
        }
    }
}