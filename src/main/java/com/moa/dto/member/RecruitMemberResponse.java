package com.moa.dto.member;

import lombok.Builder;

@Builder
public record RecruitMemberResponse(
        Long recruitMemberId,
        String recruitField,
        int currentCount,
        int totalCount
) {
}
