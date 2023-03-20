package com.moa.global.config.sub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.domain.user.UserRepository;
import com.moa.global.auth.utils.JwtService;
import com.moa.global.filter.JwtAuthenticationFilter;
import com.moa.global.filter.LoginProcessFilter;
import com.moa.global.filter.handler.GlobalFailureHandler;
import com.moa.global.filter.handler.JwtProviderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;

public class SecurityFilterBeanConfig {

    @Bean
    public JwtProviderHandler jwtProviderHandler(JwtService jwtService, UserRepository userRepository) {
        return new JwtProviderHandler(jwtService, userRepository);
    }

    @Bean
    public GlobalFailureHandler globalFailureHandler(ObjectMapper objectMapper) {
        return new GlobalFailureHandler(objectMapper);
    }

    @Bean
    public LoginProcessFilter loginProcessFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtProviderHandler jwtProviderHandler, GlobalFailureHandler globalFailureHandler) {
        LoginProcessFilter loginProcessFilter = new LoginProcessFilter(objectMapper, authenticationManager);
        loginProcessFilter.setAuthenticationSuccessHandler(jwtProviderHandler);
        loginProcessFilter.setAuthenticationFailureHandler(globalFailureHandler);
        return loginProcessFilter;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository, GlobalFailureHandler globalFailureHandler) {
        return new JwtAuthenticationFilter(jwtService, userRepository, globalFailureHandler);
    }
}
