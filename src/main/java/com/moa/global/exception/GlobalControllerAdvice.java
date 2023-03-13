package com.moa.global.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> duplicateKeyExceptionHandle(DuplicateKeyException e) {
        return new ResponseEntity<>(new ErrorResponse(400, e.getMessage()), HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> usernameNotFoundExceptionHandle(UsernameNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(400, e.getMessage()), HttpStatusCode.valueOf(400));
    }

    record ErrorResponse(int statusCode, String errorMessage) {}
}
