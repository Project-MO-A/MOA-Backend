package com.moa.global.auth.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.moa.global.auth.model.TokenMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final String PREFIX = "Bearer ";
    private final String BLANK = "";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessTokenValidationSeconds;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidationSeconds;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    public TokenMapping createToken(String email) {
        return TokenMapping.builder()
                .accessToken(createAccessToken(email))
                .refreshToken(createRefreshToken())
                .build();
    }
    public String createAccessToken(String email) {
        return PREFIX.concat(JWT.create()
                .withSubject("AccessToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidationSeconds * 1000))
                .withClaim("email", email)
                .sign(Algorithm.HMAC512(secret)));
    }

    public String createRefreshToken() {
        return PREFIX.concat(JWT.create()
                .withSubject("RefreshToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidationSeconds * 1000))
                .sign(Algorithm.HMAC512(secret)));
    }

    public void sendBothToken(HttpServletResponse response, String accessToken, String refreshToken) {
        setAccessTokenInHeader(response, accessToken);
        setRefreshTokenInHeader(response, refreshToken);
    }

    public void setAccessTokenInHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    public void setRefreshTokenInHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(PREFIX))
                .map(refreshToken -> refreshToken.replace(PREFIX, BLANK));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(PREFIX))
                .map(refreshToken -> refreshToken.replace(PREFIX, BLANK));
    }

    public String extractUserEmail(String token) {
        return JWT.require(Algorithm.HMAC512(secret))
                .build()
                .verify(token.replace(PREFIX, BLANK))
                .getClaim("email")
                .asString();
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}