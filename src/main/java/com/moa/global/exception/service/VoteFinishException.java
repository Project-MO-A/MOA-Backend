package com.moa.global.exception.service;

import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;

public class VoteFinishException extends BusinessException {
    public VoteFinishException(ErrorCode errorCode) {
        super(errorCode);
    }
}
