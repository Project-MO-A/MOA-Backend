package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class InvalidCodeException extends BusinessException {
    public InvalidCodeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
