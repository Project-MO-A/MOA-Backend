package com.moa.dto.user;

import com.moa.domain.interests.RecruitmentInterest;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import com.moa.dto.recruit.RecruitmentInfo;
import com.moa.dto.recruit.RecruitmentInterestInfo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.moa.dto.constant.RedirectURIConst.RECRUIT_INFO;

@Getter
public class UserRecruitmentInterestInfo {
    private final List<RecruitmentInterestInfo> writing;

    public UserRecruitmentInterestInfo(User user) {
        this.writing = setWriting(user);
    }

    private List<RecruitmentInterestInfo> setWriting(User user) {
        List<RecruitmentInterestInfo> result = new ArrayList<>();

        for (RecruitmentInterest recruitmentInterest : user.getRecruitmentInterests()) {
            Recruitment recruitment = recruitmentInterest.getRecruitment();
            String redirectUri = RECRUIT_INFO.of(String.valueOf(recruitment.getId()));
            result.add(new RecruitmentInterestInfo(recruitment, redirectUri));
        }
        return result;
    }
}
