package com.moa.domain.member;

import com.moa.domain.user.Popularity;
import com.moa.domain.user.User;
import com.moa.global.exception.service.InvalidCodeException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.moa.domain.member.ApprovalStatus.*;
import static com.moa.global.exception.ErrorCode.STATUS_CODE_INVALID;
import static com.moa.global.exception.ErrorCode.STATUS_CODE_REPLACE_TO_KICK;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "APPLIMENT_MEMBER")
@Entity
public class ApplimentMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLIMENT_MEMBER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "RECRUIT_MEMBER_ID")
    private RecruitMember recruitMember;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private Double popularity;

    @Builder
    public ApplimentMember(RecruitMember recruitMember, User user, ApprovalStatus status) {
        this.recruitMember = recruitMember;
        this.user = user;
        this.status = status;
        this.popularity = null;
    }

    public String changeStatus(ApprovalStatus status) {
        if (status == PENDING) throw new InvalidCodeException(STATUS_CODE_INVALID);
        changeRecruitMemberCount(status, this.recruitMember);

        this.status = status;
        return this.status.name();
    }

    public void setRecruitMember(RecruitMember recruitMember) {
        this.recruitMember = recruitMember;
    }

    public void setPopularity(double rate) {
        Popularity userPopularity = user.getPopularity();

        if (this.popularity == null) {
            userPopularity.addPopularity(rate);
        } else {
            userPopularity.updatePopularity(this.popularity, rate);
        }
        this.popularity = rate;
    }

    private void changeRecruitMemberCount(ApprovalStatus status, RecruitMember appliedRecruitMember) {
        if (this.status != APPROVED && status == APPROVED) appliedRecruitMember.addCount();
        else if (this.status == APPROVED && status == KICK) appliedRecruitMember.minusCount();
        else if (this.status == APPROVED && status == REFUSE) throw new InvalidCodeException(STATUS_CODE_REPLACE_TO_KICK);
    }
}
