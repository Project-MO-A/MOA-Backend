package com.moa.dto.user;

import com.moa.domain.interests.Interests;
import com.moa.domain.link.Link;
import com.moa.domain.user.Popularity;
import com.moa.domain.user.User;
import lombok.Builder;

import java.util.List;

@Builder
public record UserInfo(
        Long userId,
        String email,
        String name,
        String nickname,
        Double locationLatitude,
        Double locationLongitude,
        PopularityInfo popularity,
        String details,
        List<String> interests,
        List<String> link,
        String image
) {

    public UserInfo(User user, String image) {
        this(user.getEmail(), user.getName(), user.getNickname(), user.getLocationLatitude(),
                user.getLocationLongitude(), new PopularityInfo(user.getPopularity()), user.getDetails(), user.getInterests().stream().map(Interests::getName).toList(),
                user.getLink().stream().map(Link::getUrl).toList(), image);
    }

    record PopularityInfo(double rate, int count) {
        public PopularityInfo(Popularity popularity) {
            this(popularity.getRate(), popularity.getCount());
        }
    }

}