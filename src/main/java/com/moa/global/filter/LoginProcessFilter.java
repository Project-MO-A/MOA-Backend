package com.moa.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.global.filter.exception.BadValueAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Assert;

import java.io.IOException;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class LoginProcessFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_FILTER_PROCESSES_URL = "/form-login";
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
            throw new AuthenticationServiceException("Not Valid Request");
        }
    }

    record Login(String email, String password) {
        public void valid() {
            try {
                Assert.hasText(email, "email 값은 필수입니다");
                Assert.hasText(password, "password 값은 필수입니다");
            } catch (IllegalArgumentException e) {
                throw new BadValueAuthenticationException(e.getMessage());
            }
        }
    }

}
