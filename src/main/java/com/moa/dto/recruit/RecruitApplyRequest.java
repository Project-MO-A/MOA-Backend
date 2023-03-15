package com.moa.dto.recruit;

public record RecruitApplyRequest(
        Long recruitmentId,
        String position,
        Long userId
) {}
