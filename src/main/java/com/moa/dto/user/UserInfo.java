package com.moa.dto.user;

import com.moa.domain.interests.Interests;
import com.moa.domain.user.User;
import lombok.Builder;

import java.util.List;

@Builder
public record UserInfo(
        String email,
        String name,
        String nickname,
        double locationLatitude,
        double locationLongitude,
        int popularity,
        String details,
        List<String> interests
) {
    public UserInfo(User user) {
        this(user.getEmail(), user.getName(), user.getNickname(), user.getLocationLatitude(),
                user.getLocationLongitude(), user.getPopularity(), user.getDetails(), user.getInterests().stream().map(Interests::getName).toList());
    }
}