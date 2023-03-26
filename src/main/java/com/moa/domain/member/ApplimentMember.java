package com.moa.domain.member;

import com.moa.domain.user.Popularity;
import com.moa.domain.user.User;
import com.moa.global.exception.service.InvalidCodeException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.moa.domain.member.ApprovalStatus.PENDING;
import static com.moa.global.exception.ErrorCode.STATUS_CODE_INVALID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "APPLIMENT_MEMBER")
@Entity
public class ApplimentMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLIMENT_MEMBER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUIT_MEMBER_ID")
    private RecruitMember recruitMember;

    @ManyToOne(fetch = FetchType.LAZY)
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
        this.status = status;
        return this.status.name();
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
}
