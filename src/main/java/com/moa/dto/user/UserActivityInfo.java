package com.moa.dto.user;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moa.domain.member.ApprovalStatus.APPROVED;
import static com.moa.domain.recruit.RecruitStatus.CONCURRENT;
import static com.moa.domain.recruit.RecruitStatus.FINISH;

@Getter
public class UserActivityInfo {
    private Map<String, List<ApprovedInfo>> approvedProjects;
    private List<EtcInfo> etcProjects;

    public UserActivityInfo(List<ApplimentMember> info) {
        setProjects(info);
    }

    private void setProjects(List<ApplimentMember> info) {
        Map<String, List<ApprovedInfo>> approved = new HashMap<>();
        List<EtcInfo> etc = new ArrayList<>();
        approved.put(CONCURRENT.name(), new ArrayList<>());
        approved.put(FINISH.name(), new ArrayList<>());
        for (ApplimentMember applimentMember : info) {
            if (applimentMember.getStatus().equals(APPROVED)) {
                RecruitStatus key = applimentMember.getRecruitMember().getRecruitment().getStatus();
                approved.put(key.name(), setRecruitmentInfo(approved.get(key.name()), applimentMember.getRecruitMember().getRecruitment()));
                continue;
            }
            etc.add(setEtcInfo(applimentMember));
        }
        this.approvedProjects = approved;
        this.etcProjects = etc;
    }

    private static List<ApprovedInfo> setRecruitmentInfo(List<ApprovedInfo> applimentInfos, Recruitment recruitment) {
        String title = recruitment.getPost().getTitle();
        applimentInfos.add(new ApprovedInfo(title));
        return applimentInfos;
    }

    private static EtcInfo setEtcInfo(ApplimentMember applimentMember) {
        Recruitment recruitment = applimentMember.getRecruitMember().getRecruitment();
        String field = applimentMember.getRecruitMember().getRecruitField();
        return new EtcInfo(recruitment.getPost().getTitle(), field, applimentMember.getStatus().getStatus());
    }

    public record ApprovedInfo(
            String title
    ) {}

    public record EtcInfo(
            String title,
            String field,
            String status
    ) {}
}
