package com.moa.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserPwUpdateRequest(
        @Email @NotBlank String email,
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {
}
