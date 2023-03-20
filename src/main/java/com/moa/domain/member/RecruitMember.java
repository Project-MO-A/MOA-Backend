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
    public RecruitMember(String recruitField, int totalRecruitCount) {
        this.recruitField = recruitField;
        this.currentRecruitCount = 0;
        this.totalRecruitCount = totalRecruitCount;
    }

    public void setParent(Recruitment recruitment) {
        this.recruitment = recruitment;
    }
}
