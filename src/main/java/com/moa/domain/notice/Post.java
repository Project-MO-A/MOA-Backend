package com.moa.domain.notice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Post {
    private String title;
    @Lob
    @Column(columnDefinition = "clob")
    private String content;

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
