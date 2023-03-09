package com.moa.global.filter.handler;

import com.moa.domain.user.UserRepository;
import com.moa.global.auth.model.SecurityUser;
import com.moa.global.auth.model.TokenMapping;
import com.moa.global.auth.utils.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JwtProviderHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String email = ((SecurityUser) authentication.getPrincipal()).getEmail();

        TokenMapping token = jwtService.createToken(email);
        jwtService.sendBothToken(response, token.accessToken(), token.refreshToken());
        userRepository.findByEmail(email).get()
                .updateRefreshToken(token.refreshToken());
    }
}