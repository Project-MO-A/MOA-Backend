package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class NumberFormatException extends BusinessException {
    public NumberFormatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
