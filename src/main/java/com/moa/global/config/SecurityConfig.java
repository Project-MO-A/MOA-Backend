package com.moa.global.config;

import com.moa.global.config.sub.SecurityFilterBeanConfig;
import com.moa.global.config.sub.SecurityServiceBeanConfig;
import com.moa.global.filter.JwtAuthorizationFilter;
import com.moa.global.filter.LoginProcessFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import static org.springframework.http.HttpMethod.DELETE;

@Configuration
@RequiredArgsConstructor
@Import({SecurityFilterBeanConfig.class, SecurityServiceBeanConfig.class})
public class SecurityConfig {

    private final LoginProcessFilter loginProcessFilter;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .formLogin().disable();

        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(DELETE, "/user/sign-out").hasRole("USER")
                                .anyRequest().permitAll()
                );

        http.addFilterAfter(loginProcessFilter, LogoutFilter.class);
        http.addFilterAfter(jwtAuthorizationFilter, LoginProcessFilter.class);
        return http.build();
    }
}
