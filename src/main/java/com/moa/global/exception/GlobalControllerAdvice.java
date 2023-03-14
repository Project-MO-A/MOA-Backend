package com.moa.global.exception;

import com.moa.global.exception.auth.WrongPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    private static final String LOG_FORMAT = "Error Class : {}, Message : {}";

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> duplicateKeyExceptionHandle(DuplicateKeyException e) {
        return new ResponseEntity<>(new ErrorResponse(400, e.getMessage()), HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> usernameNotFoundExceptionHandle(UsernameNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(400, e.getMessage()), HttpStatusCode.valueOf(400));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(WrongPasswordException.class)
    public ExceptionResponse wrongPasswordException(WrongPasswordException e) {
        log.info(LOG_FORMAT, e.getClass(), e.getMessage());
        return new ExceptionResponse(e);
    }

    record ErrorResponse(int statusCode, String errorMessage) {}
}
