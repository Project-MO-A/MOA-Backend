package com.moa.global.filter.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.dto.user.UserIdNameResponse;
import com.moa.global.auth.model.Claims;
import com.moa.global.auth.model.SecurityUser;
import com.moa.global.auth.model.TokenMapping;
import com.moa.global.auth.token.AccessTokenProvider;
import com.moa.global.auth.token.RefreshTokenProvider;
import com.moa.global.auth.token.TokenInjector;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.moa.global.auth.utils.AuthorityToStringConvertor.convert;
import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
public class JwtProviderHandler implements AuthenticationSuccessHandler {

    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final TokenInjector tokenInjector;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Long id = securityUser.getId();

        tokenInjector.injectToken(response, createToken(id, convert(securityUser.getAuthorities())));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().write(objectMapper.writeValueAsString(new UserIdNameResponse(null, securityUser.getUsername())).getBytes(UTF_8));
    }

    private TokenMapping createToken(Long id, List<String> authorities) {
        Claims claims = new Claims(id, authorities, new Date());
        String accessToken = accessTokenProvider.createToken(claims);
        String refreshToken = refreshTokenProvider.createToken(claims);
        return new TokenMapping(accessToken, refreshToken);
    }
}