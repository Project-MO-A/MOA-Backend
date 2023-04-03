package com.moa.dto.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.user.User;
import com.moa.dto.member.RecruitMemberRequest;
import com.moa.global.exception.service.InvalidRequestException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

import static com.moa.global.exception.ErrorCode.REQUEST_INVALID;

@Builder
public record RecruitPostRequest (
        @NotBlank String title,
        @NotBlank String content,
        @NotEmpty @Valid List<RecruitMemberRequest> memberFields,
        List<String> tags
) {
    public Recruitment toEntity(User user, List<RecruitMember> members, List<RecruitTag> tags) {
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
        if (tags != null) recruitment.setTags(tags);
        return recruitment;
    }

    public List<RecruitMember> toMemberList() {
        if (memberFields == null || memberFields.isEmpty()) throw new InvalidRequestException(REQUEST_INVALID);
        return memberFields.stream()
                .map(field -> RecruitMember.builder()
                        .recruitField(field.field())
                        .totalRecruitCount(field.total())
                        .build())
                .toList();
    }
}
