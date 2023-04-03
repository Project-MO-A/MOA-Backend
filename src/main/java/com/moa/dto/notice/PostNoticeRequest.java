package com.moa.dto.notice;

import com.moa.domain.notice.Notice;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.Recruitment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record PostNoticeRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime meetingTime,
        String confirmedLocation,
        @NotNull Boolean checkVote
) {
    public Notice toEntity(Recruitment recruitment) {
        return Notice.builder()
                .recruitment(recruitment)
                .post(new Post(title, content))
                .confirmedTime(meetingTime)
                .confirmedLocation(confirmedLocation)
                .checkVote(checkVote)
                .build();
    }
}
