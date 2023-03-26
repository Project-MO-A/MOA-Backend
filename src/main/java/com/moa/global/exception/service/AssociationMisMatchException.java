package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class AssociationMisMatchException extends BusinessException {
    public AssociationMisMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
