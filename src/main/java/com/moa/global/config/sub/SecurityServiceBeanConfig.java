package com.moa.global.config.sub;

import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.global.filter.handler.RecruitmentAuthorizationManagerForApplimentMember;
import com.moa.global.filter.handler.RecruitmentAuthorizationManagerForAuthor;
import com.moa.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

public class SecurityServiceBeanConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3002", "http://13.210.190.114:8000"));
        configuration.setAllowedMethods(List.of("HEAD", "POST", "GET", "DELETE", "PUT", "OPTIONS", "PATCH"));
        configuration.addAllowedHeader("*");
        configuration.setExposedHeaders(List.of("Authorization", "AuthorizationRefresh"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

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
    public RecruitmentAuthorizationManagerForApplimentMember forApplimentMember(ApplimentMemberRepository applimentMemberRepository) {
        return new RecruitmentAuthorizationManagerForApplimentMember(applimentMemberRepository);
    }

    @Bean
    public RecruitmentAuthorizationManagerForAuthor forAuthor(RecruitmentRepository recruitmentRepository) {
        return new RecruitmentAuthorizationManagerForAuthor(recruitmentRepository);
    }
}
