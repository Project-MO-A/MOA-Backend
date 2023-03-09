package com.moa.dto.user;

import com.moa.domain.user.User;
import lombok.Builder;

@Builder
public record UserSignupRequest(
        String email,
        String password,
        String name,
        String nickname,
        String details,
        double locationLatitude,
        double locationLongitude
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

}
