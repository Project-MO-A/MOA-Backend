package com.moa.domain.member;

import com.moa.domain.notice.Notice;
import com.moa.domain.user.User;
import com.moa.global.exception.ErrorCode;
import com.moa.global.exception.service.AssociationMisMatchException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ATTEND_MEMBER")
@Entity
public class AttendMember {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private Attendance attendance;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "NOTICE_ID")
    private Notice notice;

    @Builder
    public AttendMember(Attendance attendance, User user, Notice notice) {
        this.attendance = attendance;
        this.user = user;
        this.notice = notice;
    }

    public void changeAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    public void checkRecruitment(Long recruitmentId) {
        if (!this.notice.getRecruitment().getId().equals(recruitmentId)) {
            throw new AssociationMisMatchException(ErrorCode.NOTICE_ASSOCIATION_MISMATCH);
        }
    }

    public boolean canVote() {
        return this.notice.isVote();
    }
}
