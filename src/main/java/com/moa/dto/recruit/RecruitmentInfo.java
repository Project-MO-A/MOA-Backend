package com.moa.dto.recruit;

import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RecruitmentInfo {
    private final Long id;
    private final String title;
    private final LocalDateTime createdDate;
    private final String recruitStatus;
    private final List<String> tags;

    public RecruitmentInfo(Recruitment recruitment) {
        this.id = recruitment.getId();
        this.title = recruitment.getPost().getTitle();
        this.createdDate = recruitment.getCreatedDate();
        this.recruitStatus = recruitment.getStatus().getStatus();
        this.tags = getTagNames(recruitment);
    }

    private List<String> getTagNames(Recruitment recruitment) {
        return recruitment.getTags().stream()
                .map(recruitTag -> recruitTag.getTag().getName())
                .toList();
    }
}