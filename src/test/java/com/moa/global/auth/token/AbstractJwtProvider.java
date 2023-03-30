package com.moa.global.auth.token;

import com.moa.global.auth.properties.JwtProperties;

import java.util.Map;

public abstract class AbstractJwtProvider {
    protected static final String SECRET = "secret";
    protected static final int ACCESS_TOKEN_VALIDATION_SECONDS = 1;
    protected static final int REFRESH_TOKEN_VALIDATION_SECONDS = 1;
    protected static final String ACCESS_HEADER = "Authorization";
    protected static final String REFRESH_HEADER = "AuthorizationRefresh";

    protected JwtProperties jwtProperties = new JwtProperties(SECRET,
            Map.of("AccessToken", new JwtProperties.TokenInfo(ACCESS_TOKEN_VALIDATION_SECONDS, ACCESS_HEADER),
                    "RefreshToken", new JwtProperties.TokenInfo(REFRESH_TOKEN_VALIDATION_SECONDS, REFRESH_HEADER)));
}
