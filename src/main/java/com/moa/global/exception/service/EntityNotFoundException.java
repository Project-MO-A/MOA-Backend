package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
