package com.moa.dto.user;

import com.moa.domain.interests.Interests;
import com.moa.domain.link.Link;

import java.util.List;

public record UserProfileUpdateRequest(
        Double locationLatitude,
        Double locationLongitude,
        List<String> links,
        List<String> tags,
        String details
) {
    public List<Interests> stringToInterests() {
        if (tags != null) {
            return tags.stream()
                    .map(Interests::new)
                    .toList();
        }
        return null;
    }

    public List<Link> stringToLink() {
        if (links != null) {
            return links.stream()
                    .map(Link::new)
                    .toList();
        }
        return null;
    }
}
