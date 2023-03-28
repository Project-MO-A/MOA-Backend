package com.moa.dto.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ApprovedMemberResponse {
    private final Long userId;
    private final Long applyId;
    private final String nickname;
    private final String recruitField;
    private final double popularity;
    private Long totalVote;
    private Long attend;

    public void setTotalVote(Long totalVote) {
        this.totalVote = totalVote;
    }

    public void setAttend(Long attend) {
        this.attend = attend;
    }
}
