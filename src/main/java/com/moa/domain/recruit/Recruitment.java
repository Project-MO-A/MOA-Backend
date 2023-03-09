package com.moa.domain.recruit;

import com.moa.domain.notice.Post;
import com.moa.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Recruitment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECRUIMENT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Embedded
    private Post post;

    @Enumerated(EnumType.STRING)
    private RecruitState state;

    private String Category;

    @Builder
    public Recruitment(User user, Post post, RecruitState state, String category) {
        this.user = user;
        this.post = post;
        this.state = state;
        Category = category;
    }
}
