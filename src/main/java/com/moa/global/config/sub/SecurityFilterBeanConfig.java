package com.moa.global.config.sub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.domain.user.UserRepository;
import com.moa.global.auth.utils.JwtService;
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
    public JwtProviderHandler jwtProviderHandler(JwtService jwtService, UserRepository userRepository) {
        return new JwtProviderHandler(jwtService, userRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(ObjectMapper objectMapper) {
        return new LoginFailureHandler(objectMapper);
    }

    @Bean
    public LoginProcessFilter loginProcessFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtProviderHandler jwtProviderHandler, LoginFailureHandler loginFailureHandler) {
        LoginProcessFilter loginProcessFilter = new LoginProcessFilter(objectMapper, authenticationManager);
        loginProcessFilter.setAuthenticationSuccessHandler(jwtProviderHandler);
        loginProcessFilter.setAuthenticationFailureHandler(loginFailureHandler);
        return loginProcessFilter;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }

    @Bean
    public BusinessExceptionHandlerFilter businessExceptionHandlerFilter(ObjectMapper objectMapper, MessageSourceAccessor accessor) {
        return new BusinessExceptionHandlerFilter(objectMapper, accessor);
    }
}
