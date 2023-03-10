package com.moa.global.filter;

import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
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
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationFailureHandler failureHandler;

    /**
     * accessToken 유효 -> authentication 저장
     * accessToken 만료
     *      refreshToken 유효 -> authentication 저장, accessToken 갱신
     *      refreshToken 만료 -> authentication 저장 X
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> accessToken = jwtService.extractAccessToken(request);
        if (accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        doFilterInternal(request, response, filterChain, accessToken);
    }

    private void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Optional<String> accessToken) throws ServletException, IOException {
        try {
            accessToken.filter(jwtService::isTokenValid)
                    .ifPresentOrElse(
                            this::saveAuthentication,
                            () -> checkRefreshToken(request, response)
                    );
        } catch (AuthenticationException exception) {
            failureHandler.onAuthenticationFailure(request, response, exception);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(String accessToken) {
        String email = jwtService.extractUserEmail(accessToken);
        SecurityUser securityUser = new SecurityUser(
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("회원 정보를 찾을 수 없습니다"))
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void checkRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid);

        if (refreshToken.isPresent()) {
            User user = userRepository.findByRefreshToken(refreshToken.get())
                    .orElseThrow(IllegalArgumentException::new);
            String accessToken = jwtService.createAccessToken(user.getEmail());
            jwtService.setAccessTokenInHeader(response, accessToken);
            saveAuthentication(accessToken);
        } else {
            throw new JwtTokenNotValidException("로그인을 재시도 해주세요");
        }
    }
}