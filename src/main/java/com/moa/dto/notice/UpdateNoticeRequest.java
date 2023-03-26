package com.moa.dto.notice;

import jakarta.validation.constraints.Pattern;

public record UpdateNoticeRequest(
        String title,
        String content,
        @Pattern(regexp = "^(\\d{2}.\\d{2}.\\d{2}\\s\\d{2}:\\d{2}:\\d{2})$") String meetingTime,
        String confirmedLocation,
        Boolean checkVote
) {}
