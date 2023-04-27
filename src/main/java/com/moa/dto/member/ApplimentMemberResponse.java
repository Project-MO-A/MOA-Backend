package com.moa.dto.member;

import com.moa.domain.member.ApprovalStatus;
import lombok.Builder;

@Builder
public record ApplimentMemberResponse (
        Long userId,
        Long applyId,
        String nickname,
        String recruitField,
        ApprovalStatus status
) {}
