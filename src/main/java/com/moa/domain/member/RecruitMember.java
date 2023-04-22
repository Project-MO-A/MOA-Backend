package com.moa.domain.member;

import com.moa.domain.recruit.Recruitment;
import com.moa.dto.member.RecruitMemberRequest;
import com.moa.global.exception.service.InvalidRequestException;
import com.moa.global.exception.service.MemberStatusException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.moa.global.exception.ErrorCode.*;
import static jakarta.persistence.CascadeType.ALL;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecruitMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECRUIT_MEMBER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUITMENT_ID")
    private Recruitment recruitment;

    @OneToMany(mappedBy = "recruitMember", cascade = ALL)
    private List<ApplimentMember> applimentMembers = new ArrayList<>();

    private String recruitField;

    @Column(nullable = false)
    private int currentRecruitCount;

    @Column(nullable = false)
    private int totalRecruitCount;

    @Builder
    public RecruitMember(Recruitment recruitment, String recruitField, int totalRecruitCount) {
        this.recruitment = recruitment;
        this.recruitField = recruitField;
        this.currentRecruitCount = 0;
        this.totalRecruitCount = totalRecruitCount;
    }

    public RecruitMember(Recruitment recruitment) {
        this.recruitment = recruitment;
        this.recruitField = "LEADER";
        this.currentRecruitCount = 1;
        this.totalRecruitCount = 1;
    }

    public void update(RecruitMemberRequest request) {
        this.recruitField = request.field();
        setTotal(request.total());
    }

    public void setParent(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public void addApplimentMember(ApplimentMember applimentMember) {
        if (!applimentMembers.contains(applimentMember)) {
            applimentMembers.add(applimentMember);
            applimentMember.setRecruitMember(this);
        }
    }

    public void addCount() {
        if (totalRecruitCount <= currentRecruitCount) throw new MemberStatusException(RECRUITMEMBER_FULL_COUNT);
        currentRecruitCount += 1;
    }

    public void minusCount() {
        if (currentRecruitCount <= 0 ) throw new MemberStatusException(RECRUITMEMBER_ZERO_COUNT);
        currentRecruitCount -= 1;
    }

    private void setTotal(int total) {
        if (currentRecruitCount > total) throw new InvalidRequestException(COUNT_INVALID);
        totalRecruitCount = total;
    }
}
