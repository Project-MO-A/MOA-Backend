package com.moa.dto.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.category.RecruitCategory;
import com.moa.domain.user.User;
import com.moa.dto.member.RecruitMemberResponse;
import com.moa.dto.user.UserIdNameResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class RecruitInfoResponse {
    private final String title;
    private final String content;
    private final RecruitStatus state;
    private UserIdNameResponse postUser;
    private List<String> categories;
    private List<RecruitMemberResponse> members;

    public RecruitInfoResponse(Recruitment recruitment) {
        this.title = recruitment.getPost().getTitle();
        this.content = recruitment.getPost().getContent();
        this.state = recruitment.getStatus();
        setPostUser(recruitment.getUser());
        setCategories(recruitment.getCategory());
        setMembers(recruitment.getMembers());
    }

    private void setPostUser(User user) {
        this.postUser = new UserIdNameResponse(user.getId(), user.getName());
    }

    private void setCategories(List<RecruitCategory> categories) {
        this.categories = categories.stream()
                .map(c -> c.getCategory().getName())
                .toList();
    }

    private void setMembers(List<RecruitMember> members) {
        this.members = members.stream().map(m -> RecruitMemberResponse.builder()
                .recruitField(m.getRecruitField())
                .currentCount(m.getCurrentRecruitCount())
                .totalCount(m.getTotalRecruitCount())
                .build())
                .toList();
    }
}