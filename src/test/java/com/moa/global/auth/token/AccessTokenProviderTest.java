package com.moa.global.auth.token;

import com.moa.global.auth.model.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenProviderTest extends AbstractJwtProvider{
    private final AccessTokenProvider provider = new AccessTokenProvider(jwtProperties);

    @Test
    @DisplayName("토큰을 생성한다")
    void createToken() {
        //given
        Date expiration = new Date(System.currentTimeMillis());
        Claims claims = new Claims(1L, List.of("ROLE_USER"), expiration);

        //when
        String accessToken = provider.createToken(claims);

        //then
        Claims extractClaim = provider.extractClaim(accessToken);
        assertThat(extractClaim).isEqualTo(extractClaim);
        assertThat(extractClaim.userId()).isEqualTo(1L);
        assertThat(extractClaim.role()).isEqualTo(List.of("ROLE_USER"));
        assertThat(extractClaim.expiration().getHours()).isEqualTo(new Date(expiration.getTime() + ACCESS_TOKEN_VALIDATION_SECONDS * 1000).getHours());
        assertThat(extractClaim.expiration().getMinutes()).isEqualTo(new Date(expiration.getTime() + ACCESS_TOKEN_VALIDATION_SECONDS * 1000).getMinutes());
        assertThat(extractClaim.expiration().getSeconds()).isEqualTo(new Date(expiration.getTime() + ACCESS_TOKEN_VALIDATION_SECONDS * 1000).getSeconds());
    }
    
    @Test
    @DisplayName("토큰의 정보를 확인한다")
    void getClaimFromToken() {
        //given
        Date expiration = new Date(System.currentTimeMillis());
        Claims claims = new Claims(1L, List.of("ROLE_USER"), expiration);
        String accessToken = provider.createToken(claims);

        //when
        Claims extractClaim = provider.extractClaim(accessToken);

        //then
        assertThat(extractClaim.userId()).isNotNull();
        assertThat(extractClaim.role()).isNotNull();
        assertThat(extractClaim.expiration()).isNotNull();
    }
}