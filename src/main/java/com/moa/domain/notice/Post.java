package com.moa.domain.notice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.*;

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

    public void updateTitle(String title) {
        if (title == null || title.isBlank()) return;
        this.title = title;
    }

    public void updateContent(String content) {
        if (content == null || content.isBlank()) return;
        this.content = content;
    }
}
