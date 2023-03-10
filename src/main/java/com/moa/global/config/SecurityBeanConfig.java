package com.moa.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.domain.user.UserRepository;
import com.moa.global.filter.JwtAuthorizationFilter;
import com.moa.global.filter.handler.JwtProviderHandler;
import com.moa.global.auth.utils.JwtService;
import com.moa.global.filter.LoginProcessFilter;
import com.moa.global.filter.handler.GlobalFailureHandler;
import com.moa.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeanConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder, MessageSource messageSource) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false);
        provider.setMessageSource(messageSource);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserService(userRepository, passwordEncoder);
    }

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
    public JwtAuthorizationFilter jwtAuthorizationFilter(JwtService jwtService, UserRepository userRepository, GlobalFailureHandler globalFailureHandler) {
        return new JwtAuthorizationFilter(jwtService, userRepository, globalFailureHandler);
    }
}
