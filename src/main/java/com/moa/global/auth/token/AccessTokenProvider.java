package com.moa.global.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.moa.global.auth.model.Claims;
import com.moa.global.auth.properties.JwtProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class AccessTokenProvider extends JwtProvider{

    private static final String TOKEN_NAME = "AccessToken";

    public AccessTokenProvider(JwtProperties jwtProperties) {
        super(jwtProperties);
    }

    @Override
    public String createToken(Claims claims) {
        Date expireTime = createExpireTime(claims.expiration(), TOKEN_NAME);
        return JWT.create()
                .withSubject(TOKEN_NAME)
                .withExpiresAt(expireTime)
                .withClaim("id", claims.userId())
                .withClaim("role", claims.role())
                .sign(Algorithm.HMAC512(jwtProperties.secretKey()));
    }

    @Override
    public Claims extractClaim(String accessToken) {
        Map<String, Claim> claims = JWT.decode(accessToken).getClaims();

        Long id = claims.get("id").asLong();
        List<String> role = claims.get("role").asList(String.class);
        Date exp = claims.get("exp").asDate();
        return new Claims(id, role, exp);
    }
}
