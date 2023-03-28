package com.moa.dto.member;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ApprovedPopularityRequest(
        @NotNull
        @DecimalMin("0.5")
        @DecimalMax("5.0")
        double popularity
) {
}