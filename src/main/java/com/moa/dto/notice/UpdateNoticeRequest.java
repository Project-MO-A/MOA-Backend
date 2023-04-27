package com.moa.dto.notice;

public record UpdateNoticeRequest(
        String content,
        Boolean checkVote
) {}
