package com.moa.domain.notice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Getter;

@Getter
@Embeddable
public class Post {
    private String title;
    @Lob
    @Column(columnDefinition = "clob")
    private String content;
}
