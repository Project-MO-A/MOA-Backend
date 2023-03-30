package com.moa.global.auth.token;

import com.moa.global.auth.model.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtProviderTest extends AbstractJwtProvider{

    AccessTokenProvider accessTokenProvider = new AccessTokenProvider(jwtProperties);

    @Test
    @DisplayName("토큰이 유효한 경우")
    void validToken() {
        //given
        Date expiration = new Date(System.currentTimeMillis());
        Claims claims = new Claims(1L, List.of("ROLE_USER"), expiration);
        String token = accessTokenProvider.createToken(claims);

        //when
        boolean valid = accessTokenProvider.valid(token);

        //then
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("토큰의 유효시간이 지난 경우")
    void notValidToken() throws InterruptedException {
        //given
        Date expiration = new Date(System.currentTimeMillis());
        Claims claims = new Claims(1L, List.of("ROLE_USER"), expiration);
        String token = accessTokenProvider.createToken(claims);

        //when
        Thread.sleep(2000);
        boolean valid = accessTokenProvider.valid(token);

        //then
        assertThat(valid).isFalse();
    }
}
