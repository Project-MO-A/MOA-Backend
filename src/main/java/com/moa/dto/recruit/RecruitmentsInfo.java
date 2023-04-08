package com.moa.dto.recruit;

import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
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
}