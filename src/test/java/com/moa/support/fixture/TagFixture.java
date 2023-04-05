package com.moa.support.fixture;

import com.moa.domain.recruit.tag.Tag;
import lombok.Getter;

import java.util.List;

public enum TagFixture {
    BACKEND_TAG(List.of("백엔드", "DevOps", "Infra", "Java", "CI/CD")),
    FRONTEND_TAG(List.of("프론트엔드", "React", "Nest", "TypeScript", "Figma"));

    private final List<String> tags;

    TagFixture(List<String> tags) {
        this.tags = tags;
    }

    public List<Tag> 생성() {
        return convert();
    }

    private List<Tag> convert() {
        return this.tags.stream()
                .map(Tag::new)
                .toList();
    }

    public List<String> getTags() {
        return tags;
    }
}
