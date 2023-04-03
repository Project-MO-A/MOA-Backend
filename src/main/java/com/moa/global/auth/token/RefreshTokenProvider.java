package com.moa.global.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.global.auth.model.Claims;
import com.moa.global.auth.model.SecurityUser;
import com.moa.global.auth.properties.JwtProperties;
import com.moa.global.exception.service.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.moa.global.auth.utils.AuthorityToStringConvertor.convert;
import static com.moa.global.exception.ErrorCode.USER_NOT_FOUND;

@Component
public class RefreshTokenProvider extends JwtProvider{

    private static final String TOKEN_NAME = "RefreshToken";
    private final UserRepository userRepository;

    public RefreshTokenProvider(JwtProperties jwtProperties, UserRepository userRepository) {
        super(jwtProperties);
        this.userRepository = userRepository;
    }

    @Override
    public String createToken(Claims claims) {
        String refreshToken = JWT.create()
                .withSubject(TOKEN_NAME)
                .withExpiresAt(createExpireTime(claims.expiration(), TOKEN_NAME))
                .sign(Algorithm.HMAC512(jwtProperties.secretKey()));

        userRepository.findById(claims.userId()).get()
                .updateRefreshToken(refreshToken);
        return refreshToken;
    }

    @Override
    public Claims extractClaim(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        return new Claims(user.getId(), convert(new SecurityUser(user).getAuthorities()), new Date());
    }
}
