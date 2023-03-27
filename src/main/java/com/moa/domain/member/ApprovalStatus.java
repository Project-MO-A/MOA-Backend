package com.moa.domain.member;

import com.fasterxml.jackson.annotation.JsonValue;
import com.moa.global.exception.service.InvalidCodeException;
import lombok.Getter;

import java.util.Arrays;

import static com.moa.global.exception.ErrorCode.STATUS_CODE_INVALID;

@Getter
public enum ApprovalStatus {
    PENDING(1, "대기중"),
    APPROVED(2, "승인"),
    REFUSE(3, "거절"),
    KICK(4, "강퇴");

    private final int code;
    @JsonValue
    private final String status;

    ApprovalStatus(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public static ApprovalStatus getStatus(int code) {
        return Arrays.stream(ApprovalStatus.values())
                .filter(a -> a.code == code)
                .findFirst()
                .orElseThrow(() -> new InvalidCodeException(STATUS_CODE_INVALID));
    }
}
