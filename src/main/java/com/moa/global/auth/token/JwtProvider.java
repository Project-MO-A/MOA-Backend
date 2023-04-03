package com.moa.global.auth.token;

import com.moa.global.auth.model.Claims;
import com.moa.global.auth.properties.JwtProperties;

import java.util.Date;

public abstract class JwtProvider {

    protected JwtProperties jwtProperties;

    protected JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public boolean valid(String token) {
        Date now = new Date();
        return extractClaim(token)
                .expiration()
                .after(now);
    }

    protected Date createExpireTime(Date expiration, String tokenFlag) {
        return new Date(expiration.getTime() + jwtProperties.getExpiration(tokenFlag));
    }

    public abstract String createToken(Claims claims);

    public abstract Claims extractClaim(String token);
}
