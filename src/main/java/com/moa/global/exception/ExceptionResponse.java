package com.moa.global.exception;

import lombok.Getter;

@Getter
public class ExceptionResponse {
    private final String message;

    public ExceptionResponse(String message) {
        this.message = message;
    }

    public ExceptionResponse(RuntimeException message) {
        this.message = message.getMessage();
    }
}
