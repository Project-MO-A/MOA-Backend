package com.moa.dto.recruit;

import com.moa.domain.notice.Post;
import com.moa.domain.recruit.RecruitState;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
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
    public Recruitment toEntity(User user) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .build();
         return Recruitment.builder()
                .user(user)
                .post(post)
                .state(RecruitState.RECRUITING)
                .build();
    }

    public record RecruitMemberRequest(
       String field,
       int total
    ) {
    }
}
