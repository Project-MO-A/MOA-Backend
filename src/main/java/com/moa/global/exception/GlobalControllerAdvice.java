package com.moa.global.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
