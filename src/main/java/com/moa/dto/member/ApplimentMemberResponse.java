package com.moa.dto.member;

import com.moa.domain.member.ApprovalStatus;
import lombok.Builder;

import static com.moa.dto.constant.RedirectURIConst.USER_INFO;

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
        this(userId, applyId, nickname, recruitField, status, createRedirectUri(userId));
    }

    private static String createRedirectUri(Long userId) {
        return USER_INFO.of(String.valueOf(userId));
    }
}
