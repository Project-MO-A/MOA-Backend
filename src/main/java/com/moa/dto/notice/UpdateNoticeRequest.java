package com.moa.dto.notice;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record UpdateNoticeRequest(
        String title,
        String content,
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime meetingTime,
        String confirmedLocation,
        Boolean checkVote
) {}
