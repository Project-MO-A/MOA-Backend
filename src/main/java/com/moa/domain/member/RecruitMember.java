package com.moa.domain.member;

import com.moa.domain.recruit.Recruitment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder(builderMethodName = "Leader")
    public RecruitMember(Recruitment recruitment) {
        this.recruitment = recruitment;
        this.recruitField = "LEADER";
        this.currentRecruitCount = 1;
        this.totalRecruitCount = 1;
    }

    public void setParent(Recruitment recruitment) {
        this.recruitment = recruitment;
    }
}
