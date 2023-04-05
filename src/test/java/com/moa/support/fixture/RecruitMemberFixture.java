package com.moa.support.fixture;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.util.List;

public enum RecruitMemberFixture {
    BACKEND_MEMBER("백엔드", 5),
    FRONTEND_MEMBER("프론트엔드", 3),
    DESIGNER_MEMBER("디자이너", 1),
    PM_MEMBER("PM", 1);

    private final String field;
    private final int total;

    RecruitMemberFixture(String field, int total) {
        this.field = field;
        this.total = total;
    }

    public static List<RecruitMember> FB_모집_멤버_생성() {
        return List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성());
    }

    public RecruitMember 생성() {
        return RecruitMember
                .builder()
                .recruitField(this.field)
                .totalRecruitCount(this.total)
                .build();
    }

    public RecruitMember 생성(int totalCount) {
        return RecruitMember
                .builder()
                .recruitField(this.field)
                .totalRecruitCount(totalCount)
                .build();
    }

    public String getField() {
        return field;
    }

    public int getTotal() {
        return total;
    }
}
