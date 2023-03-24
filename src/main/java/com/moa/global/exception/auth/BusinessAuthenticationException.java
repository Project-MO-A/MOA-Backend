package com.moa.global.exception.auth;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class BusinessAuthenticationException extends BusinessException {
    private final ErrorCode errorCode;

    public BusinessAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
