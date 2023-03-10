package com.moa.global.filter.exception;


import org.springframework.security.core.AuthenticationException;

public class BadValueAuthenticationException extends AuthenticationException {
    public BadValueAuthenticationException(String explanation) {
        super(explanation);
    }
}
