package com.moa.global.filter.exception;

import com.moa.global.exception.custom.BusinessException;
import com.moa.global.exception.custom.ErrorCode;

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
