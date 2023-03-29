package com.moa.global.filter.handler;

import com.moa.domain.user.UserRepository;
import com.moa.global.auth.model.SecurityUser;
import com.moa.global.auth.model.TokenMapping;
import com.moa.global.auth.utils.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class JwtProviderHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Long id = securityUser.getId();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) securityUser.getAuthorities();

        TokenMapping token = jwtService.createToken(id, authorities);
        jwtService.sendBothToken(response, token.accessToken(), token.refreshToken());
        userRepository.findById(id).get()
                .updateRefreshToken(token.refreshToken());
    }
}