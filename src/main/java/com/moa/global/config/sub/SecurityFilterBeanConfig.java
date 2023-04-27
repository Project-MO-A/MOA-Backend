package com.moa.global.config.sub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.global.auth.token.AccessTokenProvider;
import com.moa.global.auth.token.RefreshTokenProvider;
import com.moa.global.auth.token.TokenExtractor;
import com.moa.global.auth.token.TokenInjector;
import com.moa.global.filter.BusinessExceptionHandlerFilter;
import com.moa.global.filter.JwtAuthenticationFilter;
import com.moa.global.filter.LoginProcessFilter;
import com.moa.global.filter.handler.LoginFailureHandler;
import com.moa.global.filter.handler.JwtProviderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;

public class SecurityFilterBeanConfig {

    @Bean
    public JwtProviderHandler jwtProviderHandler(AccessTokenProvider accessTokenProvider, RefreshTokenProvider refreshTokenProvider, TokenInjector tokenInjector, ObjectMapper objectMapper) {
        return new JwtProviderHandler(accessTokenProvider, refreshTokenProvider, tokenInjector, objectMapper);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(ObjectMapper objectMapper, MessageSourceAccessor accessor) {
        return new LoginFailureHandler(objectMapper, accessor);
    }

    @Bean
    public LoginProcessFilter loginProcessFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtProviderHandler jwtProviderHandler, LoginFailureHandler loginFailureHandler) {
        LoginProcessFilter loginProcessFilter = new LoginProcessFilter(objectMapper, authenticationManager);
        loginProcessFilter.setAuthenticationSuccessHandler(jwtProviderHandler);
        loginProcessFilter.setAuthenticationFailureHandler(loginFailureHandler);
        return loginProcessFilter;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(TokenExtractor extractor, TokenInjector injector, AccessTokenProvider accessTokenProvider, RefreshTokenProvider refreshTokenProvider) {
        return new JwtAuthenticationFilter(extractor, injector, accessTokenProvider, refreshTokenProvider);
    }

    @Bean
    public BusinessExceptionHandlerFilter businessExceptionHandlerFilter(ObjectMapper objectMapper, MessageSourceAccessor accessor) {
        return new BusinessExceptionHandlerFilter(objectMapper, accessor);
    }
}
