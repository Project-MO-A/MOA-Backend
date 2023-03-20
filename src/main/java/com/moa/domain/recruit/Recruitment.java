package com.moa.domain.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.category.RecruitCategory;
import com.moa.domain.user.User;
import com.moa.dto.recruit.RecruitUpdateRequest;
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
    private RecruitStatus status;

    @OneToMany(mappedBy = "recruitment", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RecruitCategory> category = new ArrayList<>();

    @OneToMany(mappedBy = "recruitment", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RecruitMember> members = new ArrayList<>();

    @Builder
    public Recruitment(User user, Post post, RecruitStatus status) {
        this.user = user;
        this.post = post;
        this.status = status;
    }

    public void setCategory(List<RecruitCategory> list) {
        for (RecruitCategory recruitCategory : list) {
            if (!this.category.contains(recruitCategory)) {
                this.category.add(recruitCategory);
                recruitCategory.setParent(this);
            }
        }
    }

    public void setMembers(List<RecruitMember> list) {
        for (RecruitMember recruitMember : list) {
            if (!this.members.contains(recruitMember)) {
                this.members.add(recruitMember);
                recruitMember.setParent(this);
            }
        }
    }

    public void update(RecruitUpdateRequest updateRequest, List<RecruitCategory> categories) {
        this.post.updateTitle(updateRequest.title());
        this.post.updateContent(updateRequest.content());
        updateState(updateRequest.state());
        updateMembers(updateRequest.toMemberList());
        updateCategory(categories);
    }

    private void updateMembers(List<RecruitMember> memberList) {
        if (memberList == null || memberList.isEmpty()) return;
        this.members.clear();
        this.setMembers(memberList);
    }

    private void updateCategory(List<RecruitCategory> categories) {
        if (categories == null || categories.isEmpty()) return;
        this.category.clear();
        this.setCategory(categories);
    }

    public void updateState(Integer stateCode) {
        if (stateCode != null) this.status = RecruitStatus.getState(stateCode);
    }
}
