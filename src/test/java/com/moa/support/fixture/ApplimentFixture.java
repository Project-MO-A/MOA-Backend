package com.moa.support.fixture;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApprovalStatus;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import lombok.Getter;

import static com.moa.domain.member.ApprovalStatus.*;

public enum ApplimentFixture {
    LEADER_MEMBER(APPROVED),
    APPROVED_MEMBER(APPROVED),
    PENDING_MEMBER(PENDING),
    REFUSE_MEMBER(REFUSE),
    KICKED_MEMBER(KICK);

    private final ApprovalStatus status;

    ApplimentFixture(final ApprovalStatus status) {
        this.status = status;
    }

    public ApplimentMember 작성자_생성(final User user, final Recruitment recruitment) {
        return 생성(user, new RecruitMember(recruitment));
    }

    public ApplimentMember 생성(final User user, final RecruitMember recruitMember) {
        return ApplimentMember.builder()
                .user(user)
                .recruitMember(recruitMember)
                .status(this.status)
                .build();
    }

    public ApplimentMember 모집글_없이_생성(final User user) {
        return ApplimentMember.builder()
                .user(user)
                .status(this.status)
                .build();
    }

    public ApprovalStatus getStatus() {
        return status;
    }
}
