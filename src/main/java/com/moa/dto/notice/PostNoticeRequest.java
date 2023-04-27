package com.moa.dto.notice;

import com.moa.domain.notice.Notice;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.Recruitment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostNoticeRequest(
        @NotBlank String content,
        @NotNull Boolean checkVote
) {
    public Notice toEntity(Recruitment recruitment) {
        return Notice.builder()
                .recruitment(recruitment)
                .post(new Post("title", content))
                .checkVote(checkVote)
                .build();
    }
}
