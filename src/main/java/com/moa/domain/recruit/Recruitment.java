package com.moa.domain.recruit;

import com.moa.domain.base.BaseTimeEntity;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.user.User;
import com.moa.dto.recruit.RecruitUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Recruitment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECRUIMENT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Embedded
    private Post post;

    @Enumerated(STRING)
    private Category category;

    @Enumerated(STRING)
    private RecruitStatus status;

    @OneToMany(mappedBy = "recruitment", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RecruitTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "recruitment", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RecruitMember> members = new ArrayList<>();

    @Builder
    public Recruitment(User user, Post post, RecruitStatus status, Category category) {
        this.user = user;
        this.post = post;
        this.status = status;
        this.category = category;
    }

    public void setTags(List<RecruitTag> list) {
        for (RecruitTag recruitTag : list) {
            if (!this.tags.contains(recruitTag)) {
                this.tags.add(recruitTag);
                recruitTag.setParent(this);
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

    public void setMember(RecruitMember member) {
        if (!this.members.contains(member)) {
            this.members.add(member);
            member.setParent(this);
        }
    }

    public void update(RecruitUpdateRequest updateRequest, List<RecruitTag> tags) {
        this.post.updateTitle(updateRequest.title());
        this.post.updateContent(updateRequest.content());
        updateState(updateRequest.state());
        updateTags(tags);
    }

    private void updateTags(List<RecruitTag> tags) {
        if (tags == null || tags.isEmpty()) return;
        this.tags.clear();
        this.setTags(tags);
    }

    public void updateState(Integer stateCode) {
        if (stateCode != null) this.status = RecruitStatus.getState(stateCode);
    }
}
