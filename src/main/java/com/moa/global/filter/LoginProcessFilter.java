package com.moa.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.global.filter.exception.BusinessAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static com.moa.global.exception.custom.ErrorCode.*;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class LoginProcessFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_FILTER_PROCESSES_URL = "/user/login";
    private final ObjectMapper objectMapper;

    public LoginProcessFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager) {
        super(DEFAULT_FILTER_PROCESSES_URL, authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        validateRequest(request);
        Login loginInfo = objectMapper.readValue(request.getInputStream(), Login.class);
        loginInfo.valid();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginInfo.email, loginInfo.password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private static void validateRequest(HttpServletRequest request) {
        if (!request.getContentType().equals(APPLICATION_JSON_VALUE) && !request.getMethod().equals(POST.name())) {
            throw new BusinessAuthenticationException(BAD_HTTP_REQUEST);
        }
    }

    record Login(String email, String password) {
        public void valid() {
            if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
                throw new BusinessAuthenticationException(LOGIN_BAD_VALUE);
            }
        }
    }

}
