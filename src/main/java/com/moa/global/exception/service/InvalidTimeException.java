package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class InvalidTimeException extends BusinessException {
    public InvalidTimeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
