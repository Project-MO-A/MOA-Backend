package com.moa.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserUpdateRequest(
        @Email String email,
        String password,
        @NotNull String name,
        @NotNull String nickname,
        double locationLatitude,
        double locationLongitude,
        int popularity,
        String details
) {
}
