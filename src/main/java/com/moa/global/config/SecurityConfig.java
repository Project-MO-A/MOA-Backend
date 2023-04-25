package com.moa.global.config;

import com.moa.global.config.sub.SecurityFilterBeanConfig;
import com.moa.global.config.sub.SecurityServiceBeanConfig;
import com.moa.global.filter.BusinessExceptionHandlerFilter;
import com.moa.global.filter.JwtAuthenticationFilter;
import com.moa.global.filter.LoginProcessFilter;
import com.moa.global.filter.handler.RecruitmentAuthorizationManagerForApplimentMember;
import com.moa.global.filter.handler.RecruitmentAuthorizationManagerForAuthor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@RequiredArgsConstructor
@Import({SecurityFilterBeanConfig.class, SecurityServiceBeanConfig.class})
public class SecurityConfig {

    private final LoginProcessFilter loginProcessFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BusinessExceptionHandlerFilter businessExceptionHandlerFilter;
    private final RecruitmentAuthorizationManagerForApplimentMember forApplimentMember;
    private final RecruitmentAuthorizationManagerForAuthor forAuthor;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .httpBasic().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .formLogin().disable();

        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                //top priority
                                .requestMatchers(GET, "/recruitment/*").permitAll()
                                .requestMatchers("/recruitment/search/*").permitAll()
                                //admin
                                .requestMatchers("/recruitment/*", "/recruitment/*/apply/*", "/recruitment/*/approved/**", "/recruitment/*/notice/*", "/recruitment/*/notice/*/vote").access(forAuthor)
                                .requestMatchers(PUT, "/recruitment/*/attend/**").access(forAuthor)
                                .requestMatchers(POST, "/recruitment/*/notice").access(forAuthor)
                                //appliment member
                                .requestMatchers("/recruitment/*/time/**", "/recruitment/*/notice/*/vote/*").access(forApplimentMember)
                                .requestMatchers(GET, "/recruitment/*/notice").access(forApplimentMember)
                                //user
                                .requestMatchers("/user/withdraw","/recruitment/*/*", "/recruitment/*/reply/*", "/user/info/**", "/home/recruitment/recommend", "/recruitment").hasRole("USER")
                                .anyRequest().permitAll()
                );

        http.addFilterAfter(businessExceptionHandlerFilter, LogoutFilter.class);
        http.addFilterAfter(loginProcessFilter, BusinessExceptionHandlerFilter.class);
        http.addFilterAfter(jwtAuthenticationFilter, LoginProcessFilter.class);
        return http.build();
    }
}
