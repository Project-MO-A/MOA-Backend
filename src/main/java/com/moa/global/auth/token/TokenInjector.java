package com.moa.global.auth.token;

import com.moa.global.auth.model.TokenMapping;
import com.moa.global.auth.properties.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class TokenInjector {

    private static final String PREFIX = "Bearer ";
    private final JwtProperties jwtproperties;

    public TokenInjector(JwtProperties jwtproperties) {
        this.jwtproperties = jwtproperties;
    }

    public void injectToken(HttpServletResponse response, TokenMapping token) {
        response.addHeader(jwtproperties.getHeader("AccessToken"), PREFIX.concat(token.accessToken()));
        response.addHeader(jwtproperties.getHeader("RefreshToken"), PREFIX.concat(token.refreshToken()));
    }
}
