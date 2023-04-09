package com.moa.dto.recruit;

import com.moa.domain.recruit.Recruitment;

import java.time.format.DateTimeFormatter;

public record RecruitmentInfo(
        Long id,
        String title,
        String author,
        String category,
        String recruitStatus,
        String createAt,
        String profileImage,
        int replyCount
) {

    public static RecruitmentInfo of(Recruitment recruitment, int replyCount) {
        return new RecruitmentInfo(recruitment.getId(),
                recruitment.getPost().getTitle(),
                recruitment.getUser().getName(),
                recruitment.getCategory().getName(),
                recruitment.getStatus().getStatus(),
                recruitment.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "profileImageLink",
                replyCount
        );
    }
}