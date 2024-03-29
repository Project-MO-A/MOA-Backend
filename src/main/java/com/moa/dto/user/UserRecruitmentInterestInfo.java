package com.moa.dto.user;

import com.moa.domain.interests.RecruitmentInterest;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import com.moa.dto.recruit.RecruitmentInfo;
import lombok.Getter;

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
            result.add(new RecruitmentInfo(recruitment));
        }
        return result;
    }
}
