package com.moa.global.exception.auth;

import lombok.Getter;

@Getter
public class CustomAuthException extends RuntimeException {
    public CustomAuthException(String message) {
        super(message);
    }
}