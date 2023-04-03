package com.moa.support.fixture;

import com.moa.domain.member.AttendMember;
import com.moa.domain.member.Attendance;
import com.moa.domain.notice.Notice;
import com.moa.domain.user.User;
import lombok.Getter;

import static com.moa.domain.member.Attendance.ATTENDANCE;
import static com.moa.domain.member.Attendance.NONATTENDANCE;

@Getter
public enum AttendMemberFixture {
    ATTENDANCE_MEMBER(ATTENDANCE),
    NON_ATTENDANCE_MEMBER(NONATTENDANCE);

    private final Attendance attendance;

    AttendMemberFixture(Attendance attendance) {
        this.attendance = attendance;
    }

    public AttendMember 생성(User user, Notice notice) {
        return AttendMember.builder()
                .user(user)
                .notice(notice)
                .attendance(this.attendance)
                .build();
    }
}
