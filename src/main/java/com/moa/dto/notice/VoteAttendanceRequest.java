package com.moa.dto.notice;

import com.moa.domain.member.Attendance;
import lombok.Builder;

public record VoteAttendanceRequest(
        Long recruitmentId,
        Long noticeId,
        Long userId,
        Attendance attendance
) {
    @Builder
    public VoteAttendanceRequest(Long recruitmentId, Long noticeId, Long userId, String attendance) {
        this(recruitmentId, noticeId, userId, Attendance.valueOf(attendance));
    }
}
