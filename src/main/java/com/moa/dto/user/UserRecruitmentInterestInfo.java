package com.moa.dto.user;

import com.moa.domain.interests.RecruitmentInterest;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class UserRecruitmentInterestInfo {
    private final List<RecruitmentInfo> writing;

    public UserRecruitmentInterestInfo(User user) {
        this.writing = setWriting(user);
    }

    private List<RecruitmentInfo> setWriting(User user) {
        List<RecruitmentInfo> result = new ArrayList<>();

        for (RecruitmentInterest recruitmentInterest : user.getRecruitmentInterests()) {
            Recruitment recruitment = recruitmentInterest.getRecruitment();
            result.add(RecruitmentInfo.of(recruitment, 0));
        }
        return result;
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
