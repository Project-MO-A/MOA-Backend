package com.moa.dto.member;

import jakarta.validation.constraints.NotNull;

public record ApprovedPopularityRequest(
        @NotNull double popularity
) {
}
