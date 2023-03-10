package com.moa.dto.user;

import com.moa.domain.interests.Interests;
import com.moa.domain.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record UserSignupRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String name,
        String nickname,
        String details,
        double locationLatitude,
        double locationLongitude,
        List<String> interests
) {
    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .details(details)
                .locationLatitude(locationLatitude)
                .locationLongitude(locationLongitude)
                .build();
    }

    public List<Interests> getInterests() {
        if (interests != null) {
            return interests.stream()
                    .map(Interests::new)
                    .toList();
        }
        return null;
    }

}
