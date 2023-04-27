package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class DuplicateApplyException extends BusinessException {
    public DuplicateApplyException(ErrorCode errorCode) {
        super(errorCode);
    }
}