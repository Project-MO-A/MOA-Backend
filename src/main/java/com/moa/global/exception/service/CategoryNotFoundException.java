package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
