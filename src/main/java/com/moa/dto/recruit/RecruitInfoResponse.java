package com.moa.dto.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.user.User;
import com.moa.dto.member.RecruitMemberResponse;
import com.moa.dto.user.UserIdNameResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class RecruitInfoResponse {
    private final String title;
    private final String content;
    private final int state;
    private final String category;
    private UserIdNameResponse postUser;
    private List<String> tags;
    private List<RecruitMemberResponse> members;

    public RecruitInfoResponse(Recruitment recruitment) {
        this.title = recruitment.getPost().getTitle();
        this.content = recruitment.getPost().getContent();
        this.state = recruitment.getStatus().getCode();
        this.category = recruitment.getCategory().getName();
        setPostUser(recruitment.getUser());
        setTags(recruitment.getTags());
        setMembers(recruitment.getMembers());
    }

    private void setPostUser(User user) {
        this.postUser = new UserIdNameResponse(user.getId(), user.getName());
    }

    private void setTags(List<RecruitTag> tags) {
        this.tags = tags.stream()
                .map(c -> c.getTag().getName())
                .toList();
    }

    private void setMembers(List<RecruitMember> members) {
        this.members = members.stream().map(m -> RecruitMemberResponse.builder()
                        .recruitMemberId(m.getId())
                        .recruitField(m.getRecruitField())
                        .currentCount(m.getCurrentRecruitCount())
                        .totalCount(m.getTotalRecruitCount())
                        .build())
                .toList();
    }
}
