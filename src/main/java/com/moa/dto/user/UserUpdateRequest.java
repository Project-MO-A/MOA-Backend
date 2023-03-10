package com.moa.dto.user;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record UserUpdateRequest(
        String password,
        String name,
        String nickname,
        @Positive double locationLatitude,
        @Positive double locationLongitude,
        @Positive int popularity,
        String details
) {
}
