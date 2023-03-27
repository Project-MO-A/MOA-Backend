package com.moa.dto.user;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moa.domain.member.Approval.APPROVED;
import static com.moa.domain.recruit.RecruitStatus.*;

@Getter
public class UserActivityInfo {
    private final Map<String, List<RecruitmentInfo>> activity;

    public UserActivityInfo(List<ApplimentMember> info) {
        this.activity = setActivity(info);
    }

    private Map<String, List<RecruitmentInfo>> setActivity(List<ApplimentMember> info) {
        Map<String, List<RecruitmentInfo>> result = new HashMap<>();
        result.put(COMPLETE.name(), new ArrayList<>());
        result.put(FINISH.name(), new ArrayList<>());
        for (ApplimentMember applimentMember : info) {
            if (applimentMember.getApproval().equals(APPROVED)) {
                RecruitStatus key = applimentMember.getRecruitMember().getRecruitment().getStatus();
                result.put(key.name(), setRecruitmentInfo(result.get(key), applimentMember.getRecruitMember().getRecruitment()));
            }
        }
        return result;
    }

    private static List<RecruitmentInfo> setRecruitmentInfo(List<RecruitmentInfo> applimentInfos, Recruitment recruitment) {
        String title = recruitment.getPost().getTitle();
        String redirectUri = "/recruitment/".concat(String.valueOf(recruitment.getId()));
        applimentInfos.add(new RecruitmentInfo(title, redirectUri));
        return applimentInfos;
    }

    record RecruitmentInfo(
            String title,
            String redirectUri
    ) {}
}
