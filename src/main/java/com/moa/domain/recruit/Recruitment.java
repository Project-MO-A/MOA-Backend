package com.moa.domain.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.category.RecruitCategory;
import com.moa.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "recruitment", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RecruitCategory> category = new ArrayList<>();

    @OneToMany(mappedBy = "recruitment", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RecruitMember> members = new ArrayList<>();

    @Builder
    public Recruitment(User user, Post post, RecruitState state) {
        this.user = user;
        this.post = post;
        this.state = state;
    }

    public void setCategory(List<RecruitCategory> list) {
        this.category=list;
        for (RecruitCategory recruitCategory : list) {
            recruitCategory.setParent(this);
        }
    }

    public void setMembers(List<RecruitMember> list) {
        this.members=list;
        for (RecruitMember recruitMember : list) {
            recruitMember.setParent(this);
        }
    }
}
