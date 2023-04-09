package com.moa.dto.recruit;

import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RecruitmentsInfo {
    private final List<RecruitmentInfo> writing;

    public RecruitmentsInfo(List<Recruitment> recruitments) {
        this.writing = setRecruitPostInfo(recruitments);
    }

    private List<RecruitmentInfo> setRecruitPostInfo(List<Recruitment> recruitments) {
        List<RecruitmentInfo> infos = new ArrayList<>();
        for (Recruitment recruitment : recruitments) {
            infos.add(RecruitmentInfo.of(recruitment, 0));
        }
        return infos;
    }
}