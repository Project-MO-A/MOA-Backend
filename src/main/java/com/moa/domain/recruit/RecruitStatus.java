package com.moa.domain.recruit;

import com.moa.global.exception.service.InvalidCodeException;
import lombok.Getter;

import java.util.Arrays;

import static com.moa.global.exception.ErrorCode.STATUS_CODE_INVALID;

@Getter
public enum RecruitStatus {
    RECRUITING(1, "모집중"),
    COMPLETE(2, "모집완료"),
    FINISH(3, "종료");

    private final int code;
    private final String status;

    RecruitStatus(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public static RecruitStatus getState(int stateCode) {
        return Arrays.stream(RecruitStatus.values())
                .filter(state -> state.getCode() == stateCode)
                .findFirst()
                .orElseThrow(() -> new InvalidCodeException(STATUS_CODE_INVALID));
    }
}
