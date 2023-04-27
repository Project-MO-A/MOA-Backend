package com.moa.global.filter;

import com.moa.global.auth.token.AccessTokenProvider;
import com.moa.global.auth.token.RefreshTokenProvider;
import com.moa.global.auth.token.TokenExtractor;
import com.moa.global.auth.token.TokenInjector;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static com.moa.global.filter.constant.FilterConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private TokenExtractor extractor;
    @Mock
    private TokenInjector injector;
    @Mock
    private AccessTokenProvider accessTokenProvider;
    @Mock
    private RefreshTokenProvider refreshTokenProvider;
    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void init() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("AccessToken - 없음")
    void hasNoAccessToken() throws ServletException, IOException {
        //given
        given(extractor.extractToken(request)).willReturn(notValidBothTokens);

        //when
        filter.doFilterInternal(request, response, filterChain);

        //then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("AccessToken - 인증 성공")
    void successAuthentication() throws ServletException, IOException {
        //given
        given(extractor.extractToken(request)).willReturn(tokens);
        given(accessTokenProvider.valid(tokens.accessToken())).willReturn(true);
        given(accessTokenProvider.extractClaim(tokens.accessToken())).willReturn(claims);

        //when
        filter.doFilterInternal(request, response, filterChain);

        //then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    @DisplayName("AccessToken - 인증 실패 & RefreshToken - 인증 성공")
    void successAuthenticationByRefreshToken() throws ServletException, IOException {
        //given
        given(extractor.extractToken(request)).willReturn(tokens);
        given(accessTokenProvider.valid(tokens.accessToken())).willReturn(false);
        given(refreshTokenProvider.extractClaim(tokens.refreshToken())).willReturn(claims);
        given(accessTokenProvider.extractClaim(tokens.accessToken())).willReturn(claims);
        given(accessTokenProvider.createToken(claims)).willReturn(tokens.accessToken());

        //when
        filter.doFilterInternal(request, response, filterChain);

        //then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    @DisplayName("전체 인증 실패, AccessToken - 인증 실패 & RefreshToken - 인증 실패")
    void failAuthentication() throws ServletException, IOException {
        //given
        given(extractor.extractToken(request)).willReturn(notValidRefreshTokens);
        given(accessTokenProvider.valid(notValidRefreshTokens.accessToken())).willReturn(false);

        //when
        filter.doFilterInternal(request, response, filterChain);

        //then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

}