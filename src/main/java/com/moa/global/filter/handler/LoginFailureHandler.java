package com.moa.global.filter.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private final MessageSourceAccessor accessor;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        if (exception instanceof UsernameNotFoundException e) {
            String[] codes = e.getMessage().split(" ");
            writer.println(objectMapper.writeValueAsString(
                    new ErrorResponse(codes[0], accessor.getMessage(codes[1])))
            );
        } else if (exception instanceof BadCredentialsException e) {
            writer.println(objectMapper.writeValueAsString(
                    new ErrorResponse("A0001", e.getMessage()))
            );
        }
    }
}
