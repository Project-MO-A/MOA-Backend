package com.moa.global.auth.utils;

import com.moa.global.auth.properties.JwtProperties;
import com.moa.global.auth.properties.JwtProperties.TokenInfo;
import com.moa.global.auth.model.TokenMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "secret";
    private static final int ACCESS_TOKEN_VALIDATION_SECONDS = 1;
    private static final int REFRESH_TOKEN_VALIDATION_SECONDS = 1;
    private static final String ACCESS_HEADER = "Authorization";
    private static final String REFRESH_HEADER = "AuthorizationRefresh";

    private final JwtService jwtService = new JwtService(
            new JwtProperties(
                    SECRET,
                    Map.of("access", new TokenInfo(ACCESS_TOKEN_VALIDATION_SECONDS, ACCESS_HEADER),
                            "refresh", new TokenInfo(REFRESH_TOKEN_VALIDATION_SECONDS, REFRESH_HEADER)))
    );

    @Test
    @DisplayName("토큰 생성")
    void createToken() {
        //given
        String email = "user@email.com";

        //when
        TokenMapping token = jwtService.createToken(email);

        //then
        assertThat(token.accessToken()).isNotNull();
        assertThat(token.refreshToken()).isNotNull();
    }

    @Test
    @DisplayName("토큰 헤더 통해 전달")
    void sendToken() {
        //given
        String email = "user@email.com";

        //when
        TokenMapping token = jwtService.createToken(email);
        MockHttpServletResponse response = new MockHttpServletResponse();
        jwtService.sendBothToken(response, token.accessToken(), token.refreshToken());

        //then
        String accessToken = response.getHeader(ACCESS_HEADER);
        String refreshToken = response.getHeader(REFRESH_HEADER);

        assertThat(token.accessToken()).isEqualTo(accessToken);
        assertThat(token.refreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("전달 받은 토큰 추출")
    void extractToken() {
        //given
        String sendAccessToken = "Bearer accessToken";
        String sendRefreshToken = "Bearer refreshToken";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ACCESS_HEADER, sendAccessToken);
        request.addHeader(REFRESH_HEADER, sendRefreshToken);

        //when
        Optional<String> accessToken = jwtService.extractToken(request, ACCESS_HEADER);
        Optional<String> refreshToken = jwtService.extractToken(request, REFRESH_HEADER);

        //then
        assertThat(accessToken.isPresent()).isTrue();
        assertThat(accessToken.get()).isEqualTo(sendAccessToken.replace("Bearer ", ""));
        assertThat(refreshToken.isPresent()).isTrue();
        assertThat(refreshToken.get()).isEqualTo(sendRefreshToken.replace("Bearer ", ""));
    }

    @Test
    @DisplayName("유효하지 않은 토큰 전달받으면 Optional.empty 반환")
    void invalidToken() {
        //given
        String sendAccessToken = "invalid";
        String sendRefreshToken = "invalid";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ACCESS_HEADER, sendAccessToken);
        request.addHeader(REFRESH_HEADER, sendRefreshToken);

        //when
        Optional<String> accessToken = jwtService.extractToken(request, ACCESS_HEADER);
        Optional<String> refreshToken = jwtService.extractToken(request, REFRESH_HEADER);

        //then
        assertThat(accessToken.isEmpty()).isTrue();
        assertThat(refreshToken.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("유저 이메일 정보 확인")
    void extractUserEmail() {
        //given
        String email = "user@email.com";
        String accessToken = jwtService.createAccessToken(email);

        //when
        String extractEmail = jwtService.extractUserEmail(accessToken);

        //then
        assertThat(extractEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("유효 시간 지나지 않은 토큰 사용")
    void useValidToken() {
        //given
        String email = "user@email.com";
        TokenMapping token = jwtService.createToken(email);

        //when
        boolean isAccessTokenValid = jwtService.isTokenValid(token.accessToken().replace("Bearer ", ""));
        boolean isRefreshTokenValid = jwtService.isTokenValid(token.refreshToken().replace("Bearer ", ""));

        //then
        assertThat(isAccessTokenValid).isTrue();
        assertThat(isRefreshTokenValid).isTrue();
    }

    @Test
    @DisplayName("유효 시간 지난 토큰 사용 검증 실패")
    void useInvalidToken() throws InterruptedException {
        //given
        String email = "user@email.com";
        TokenMapping token = jwtService.createToken(email);

        //when
        sleep(1000);
        boolean isAccessTokenValid = jwtService.isTokenValid(token.accessToken().replace("Bearer ", ""));
        boolean isRefreshTokenValid = jwtService.isTokenValid(token.refreshToken().replace("Bearer ", ""));

        //then
        assertThat(isAccessTokenValid).isFalse();
        assertThat(isRefreshTokenValid).isFalse();
    }

}