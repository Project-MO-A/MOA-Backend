package com.moa.dto.notice;

import com.moa.domain.notice.Notice;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.Recruitment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record PostNoticeRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotBlank @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})$") String meetingTime,
        String confirmedLocation,
        @NotNull Boolean checkVote
) {
    public Notice toEntity(Recruitment recruitment) {
        return Notice.builder()
                .recruitment(recruitment)
                .post(new Post(title, content))
                .confirmedTime(LocalDateTime.parse(meetingTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .confirmedLocation(confirmedLocation)
                .checkVote(checkVote)
                .build();
    }
}
