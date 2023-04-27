package com.moa.dto.user;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moa.domain.member.ApprovalStatus.APPROVED;
import static com.moa.domain.recruit.RecruitStatus.*;

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
            RecruitMember recruitMember = applimentMember.getRecruitMember();
            RecruitStatus status = recruitMember.getRecruitment().getStatus();
            if (status.equals(RECRUITING)) {
                etc.add(setEtcInfo(applimentMember));
                continue;
            }
            if (applimentMember.getStatus().equals(APPROVED)) {
                approved.put(status.name(), setRecruitmentInfo(approved.get(status.name()), recruitMember.getRecruitment()));
            }
        }
        this.approvedProjects = approved;
        this.etcProjects = etc;
    }

    private static List<ApprovedInfo> setRecruitmentInfo(List<ApprovedInfo> applimentInfos, Recruitment recruitment) {
        applimentInfos.add(new ApprovedInfo(recruitment.getId(), recruitment.getPost().getTitle()));
        return applimentInfos;
    }

    private static EtcInfo setEtcInfo(ApplimentMember applimentMember) {
        Recruitment recruitment = applimentMember.getRecruitMember().getRecruitment();
        String field = applimentMember.getRecruitMember().getRecruitField();
        return new EtcInfo(recruitment.getId(), recruitment.getPost().getTitle(), field, applimentMember.getStatus().getStatus());
    }

    public record ApprovedInfo(
            Long recruitmentId,
            String title
    ) {}

    public record EtcInfo(
            Long recruitmentId,
            String title,
            String field,
            String status
    ) {}
}
