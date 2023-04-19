package com.moa.dto.user;

import com.moa.domain.interests.Interests;
import com.moa.domain.link.Link;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Builder
public record UserUpdateRequest(
        @Email @NotBlank String email,
        @Length(min = 1, max = 15) String name,
        @Length(min = 1, max = 15) String nickname,
        @Positive double locationLatitude,
        @Positive double locationLongitude,
        String details,
        List<String> interests,
        List<String> links
) {
    public List<Interests> interestsValue() {
        if (interests != null) {
            return interests.stream()
                    .map(Interests::new)
                    .toList();
        }
        return null;
    }

    public List<Link> linksValue() {
        if (links != null) {
            return links.stream()
                    .map(Link::new)
                    .toList();
        }
        return null;
    }
}
