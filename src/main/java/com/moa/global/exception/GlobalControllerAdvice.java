package com.moa.global.exception;

import com.moa.global.exception.auth.WrongPasswordException;
import com.moa.global.exception.custom.BusinessException;
import com.moa.global.exception.custom.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private static final String LOG_FORMAT = "Error Class : {}, Message : {}";
    private final MessageSourceAccessor messageSourceAccessor;

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(WrongPasswordException.class)
    public ErrorResponse wrongPasswordException(WrongPasswordException e) {
        log.info(LOG_FORMAT, e.getClass(), e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessExceptionHandler(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        String code = errorCode.getCode();
        String message = messageSourceAccessor.getMessage(errorCode.getMessageCode());
        HttpStatus status = HttpStatus.valueOf(errorCode.getStatusCode());

        return new ResponseEntity<>(new ErrorResponse(code, message), status);
    }
}
