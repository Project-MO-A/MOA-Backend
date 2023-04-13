package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class ReplyAuthorityException extends BusinessException {
    public ReplyAuthorityException(ErrorCode errorCode) {
        super(errorCode);
    }
}
