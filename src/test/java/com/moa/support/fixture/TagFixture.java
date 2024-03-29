package com.moa.support.fixture;

import com.moa.domain.recruit.tag.Tag;

import java.util.List;

public enum TagFixture {
    BACKEND_TAG(List.of("백엔드", "DevOps", "Infra", "Java", "CI/CD")),
    FRONTEND_TAG(List.of("프론트엔드", "React", "Nest", "TypeScript", "Figma")),

    BOOK_TAG(List.of("독서", "교양", "힐링", "자기개발")),
    TOEIC_TAG(List.of("토익", "영어", "자격증"))
    ;

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
