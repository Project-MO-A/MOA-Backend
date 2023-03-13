package com.moa.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserUpdateRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank @Length(min = 1, max = 15) String name,
        @NotBlank @Length(min = 1, max = 15) String nickname,
        @Positive double locationLatitude,
        @Positive double locationLongitude,
        @Positive int popularity,
        String details
) {
}
