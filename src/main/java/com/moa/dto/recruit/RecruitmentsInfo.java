package com.moa.dto.recruit;

import com.moa.domain.recruit.Recruitment;
import lombok.Builder;
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
            infos.add(RecruitmentInfo.builder()
                    .id(recruitment.getId())
                    .title(recruitment.getPost().getTitle())
                    .recruitStatus(recruitment.getStatus().getStatus())
                    .build());
        }
        return infos;
    }
    
    @Builder
    public record RecruitmentInfo(
            Long id,
            String title,
            String recruitStatus
    ) {}
}