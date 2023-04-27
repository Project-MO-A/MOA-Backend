package com.moa.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record RecruitMemberRequest(
        Long recruitMemberId,
        @NotBlank String field,
        @Positive int total
) {
}
