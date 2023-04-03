package com.moa.global.auth.token;

import com.moa.global.auth.model.TokenMapping;
import com.moa.global.auth.properties.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {

    private static final String PREFIX = "Bearer ";
    private static final String BLANK = "";
    private final JwtProperties jwtProperties;

    public TokenExtractor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public TokenMapping extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(jwtProperties.getHeader("AccessToken"));
        String authorizationRefreshHeader = request.getHeader(jwtProperties.getHeader("RefreshToken"));

        String accessToken = separatePrefix(authorizationHeader);
        String refreshToken = separatePrefix(authorizationRefreshHeader);
        return new TokenMapping(accessToken, refreshToken);
    }

    private String separatePrefix(String requestHeader) {
        if (requestHeader != null && requestHeader.startsWith(PREFIX)) {
            return requestHeader.replace(PREFIX, BLANK);
        }
        return "";
    }
}
