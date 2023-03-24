package com.moa.domain.member;

import com.moa.global.exception.service.InvalidCodeException;
import lombok.Getter;

import java.util.Arrays;

import static com.moa.global.exception.ErrorCode.STATUS_CODE_INVALID;

@Getter
public enum Approval {
    PENDING(1, "대기중"),
    APPROVED(2, "승인"),
    REFUSE(3, "거절");

    private final int code;
    private final String status;

    Approval(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public static Approval getInstance(int code) {
        return Arrays.stream(Approval.values())
                .filter(a -> a.code == code)
                .findFirst()
                .orElseThrow(() -> new InvalidCodeException(STATUS_CODE_INVALID));
    }
}
