package com.moa.global.auth.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.moa.global.auth.model.Claims;
import com.moa.global.auth.properties.JwtProperties;
import com.moa.global.auth.model.TokenMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtService {

    private static final String PREFIX = "Bearer ";
    private static final String BLANK = "";
    private final JwtProperties jwtProperties;

    public TokenMapping createToken(Long id, List<GrantedAuthority> authorities) {
        return TokenMapping.builder()
                .accessToken(createAccessToken(id, authorities))
                .refreshToken(createRefreshToken())
                .build();
    }

    public String createAccessToken(Long id, List<GrantedAuthority> authorities) {
        List<String> role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return JWT.create()
                .withSubject("AccessToken")
                .withExpiresAt(createExpireTime("access"))
                .withClaim("id", id)
                .withClaim("role", role)
                .sign(Algorithm.HMAC512(jwtProperties.secretKey()));
    }

    public String createRefreshToken() {
        return JWT.create()
                .withSubject("RefreshToken")
                .withExpiresAt(createExpireTime("refresh"))
                .sign(Algorithm.HMAC512(jwtProperties.secretKey()));
    }

    public void sendBothToken(HttpServletResponse response, String accessToken, String refreshToken) {
        setAccessTokenInHeader(response, PREFIX.concat(accessToken));
        setRefreshTokenInHeader(response, PREFIX.concat(refreshToken));
    }

    public void setAccessTokenInHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(jwtProperties.getHeader("access"), accessToken);
    }

    public void setRefreshTokenInHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(jwtProperties.getHeader("refresh"), refreshToken);
    }

    public Optional<String> extractToken(HttpServletRequest request, String headerName) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(token -> token.startsWith(PREFIX))
                .map(token -> token.replace(PREFIX, BLANK));
    }

    public Claims extractClaims(String token) {
        Map<String, Claim> claims = JWT.decode(token)
                .getClaims();

        Long id = claims.get("id").asLong();
        List<String> role = claims.get("role").asList(String.class);
        return new Claims(id, role);
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(jwtProperties.secretKey()))
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private Date createExpireTime(String tokenFlag) {
        return new Date(System.currentTimeMillis() + jwtProperties.getExpiration(tokenFlag));
    }
}