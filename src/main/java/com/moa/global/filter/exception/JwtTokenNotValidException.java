package com.moa.global.filter.exception;


import org.springframework.security.core.AuthenticationException;

public class JwtTokenNotValidException extends AuthenticationException {
    public JwtTokenNotValidException(String message) {
        super(message);
    }
}
