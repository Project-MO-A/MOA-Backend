package com.moa.domain.reply;

import com.moa.domain.base.BaseTimeEntity;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Reply extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPLY_ID")
    private Long id;

    private String content;

    @Column(name = "PARENT_REPLY_ID")
    private Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUIMENT_ID")
    private Recruitment recruitment;

    @Builder
    public Reply(String content, Long parentId, User user, Recruitment recruitment) {
        this.content = content;
        this.parentId = parentId;
        this.user = user;
        this.recruitment = recruitment;
    }

    public void update(String content) {
        this.content = content;
    }
}
