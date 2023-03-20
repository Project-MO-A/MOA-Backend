package com.moa.dto.member;

import lombok.Builder;

@Builder
public record RecruitMemberResponse(
        String recruitField,
        int currentCount,
        int totalCount
) {
}
