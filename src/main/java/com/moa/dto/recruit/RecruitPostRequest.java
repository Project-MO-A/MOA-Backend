package com.moa.dto.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.category.RecruitCategory;
import com.moa.domain.user.User;
import com.moa.dto.member.RecruitMemberRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record RecruitPostRequest (
        @NotBlank String title,
        @NotBlank String content,
        List<RecruitMemberRequest> memberFields,
        List<String> category
) {
    public Recruitment toEntity(User user, List<RecruitMember> members, List<RecruitCategory> categories) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();
        Recruitment recruitment = Recruitment.builder()
                .user(user)
                .post(post)
                .status(RecruitStatus.RECRUITING)
                .build();
        if (members != null) recruitment.setMembers(members);
        if (categories != null) recruitment.setCategory(categories);
        return recruitment;
    }

    public List<RecruitMember> toMemberList() {
        if (memberFields == null || memberFields.isEmpty()) return null;
        return memberFields.stream()
                .map(field -> RecruitMember.builder()
                        .recruitField(field.field())
                        .totalRecruitCount(field.total())
                        .build())
                .toList();
    }
}
