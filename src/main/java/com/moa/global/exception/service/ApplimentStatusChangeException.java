package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class ApplimentStatusChangeException extends BusinessException {
    public ApplimentStatusChangeException(ErrorCode errorCode) {
        super(errorCode);
    }
}