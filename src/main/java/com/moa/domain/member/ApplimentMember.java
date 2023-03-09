package com.moa.domain.member;

import com.moa.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Approval approval;

    @Builder
    public ApplimentMember(RecruitMember recruitMember, User user, Approval approval) {
        this.recruitMember = recruitMember;
        this.user = user;
        this.approval = approval;
    }
}
