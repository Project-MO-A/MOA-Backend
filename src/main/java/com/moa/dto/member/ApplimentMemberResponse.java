package com.moa.dto.member;

import com.moa.domain.member.ApprovalStatus;
import lombok.Builder;

public record ApplimentMemberResponse (
        Long userId,
        Long applyId,
        String nickname,
        String recruitField,
        ApprovalStatus status,
        String redirectUri
) {

    @Builder
    public ApplimentMemberResponse(Long userId, Long applyId, String nickname, String recruitField, ApprovalStatus status) {
        this(userId, applyId, nickname, recruitField, status, "/user/info/profile?userId=".concat(String.valueOf(userId)));
    }
}
