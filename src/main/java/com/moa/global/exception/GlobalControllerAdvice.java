package com.moa.global.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.GONE;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private static final String LOG_FORMAT = "Error Class : {}, Error Code : {}, Message : {}";
    private final MessageSourceAccessor messageSourceAccessor;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessExceptionHandler(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        String code = errorCode.getCode();
        String message = messageSourceAccessor.getMessage(errorCode.getMessageCode());
        HttpStatus status = HttpStatus.valueOf(errorCode.getStatusCode());

        log.error(LOG_FORMAT, e.getClass().getSimpleName(), code, message);
        return new ResponseEntity<>(new ErrorResponse(code, message), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        HttpStatusCode statusCode = e.getStatusCode();
        log.error(LOG_FORMAT, e.getClass(), statusCode.value(), message);
        return new ResponseEntity<>(new ErrorResponse(String.valueOf(statusCode.value()), message), statusCode);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> httpMessageExceptionHandler(HttpMessageNotReadableException e) {
        int code = BAD_REQUEST.value();
        log.error(LOG_FORMAT, e.getClass(), code,  e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(String.valueOf(code), e.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        log.error(LOG_FORMAT, e.getClass(), 500,  e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getClass().getName(), e.getMessage()), GONE);
    }
}
