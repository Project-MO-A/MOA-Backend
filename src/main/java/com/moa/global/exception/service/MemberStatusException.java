package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class MemberStatusException extends BusinessException {
    public MemberStatusException(ErrorCode errorCode) {
        super(errorCode);
    }
}
