package com.moa.global.config;

import com.moa.global.filter.LoginProcessFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import static org.springframework.http.HttpMethod.POST;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginProcessFilter loginProcessFilter;

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
                                .requestMatchers(POST, "/sign-out").hasRole("USER")
                                .anyRequest().permitAll()
                );

        http.addFilterAfter(loginProcessFilter, LogoutFilter.class);
        return http.build();
    }
}
