package com.moa.dto.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ApprovedMemberResponse {
    private final Long userId;
    private final Long applyId;
    private final String nickname;
    private final Long recruitMemberId;
    private final String recruitField;
    private final double popularity;
    private Long totalAttend;
    private Long attend;

    public void setTotalAttend(Long totalAttend) {
        this.totalAttend = totalAttend;
    }

    public void setAttend(Long attend) {
        this.attend = attend;
    }
}
