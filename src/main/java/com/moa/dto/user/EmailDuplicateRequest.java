package com.moa.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailDuplicateRequest(
        @NotNull @Email String email
) {
}
