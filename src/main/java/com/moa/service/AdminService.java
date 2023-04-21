package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentSearchRepository;
import com.moa.domain.member.ApprovalStatus;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.global.exception.service.ApplimentStatusChangeException;
import com.moa.global.exception.service.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.moa.domain.member.ApprovalStatus.*;
import static com.moa.global.exception.ErrorCode.*;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminService implements ApplimentHandler {
    private final ApplimentSearchRepository adminRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ApplimentMemberResponse> getApplimentMembers(final Long recruitmentId, final ApprovalStatus status) {
        validRecruitmentId(recruitmentId);
        return adminRepository.findAllApplimentMembers(recruitmentId, status);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ApprovedMemberResponse> getApprovedMembers(final Long recruitmentId) {
        validRecruitmentId(recruitmentId);
        return adminRepository.findAllApprovedMembers(recruitmentId);
    }

    @Override
    public String changeApplimentStatus(final Long applyId, final ApprovalStatus status) {
        ApplimentMember applimentMember = adminRepository.findApplimentMemberById(applyId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));
        validNotLeader(applimentMember.getRecruitMember());

        return applimentMember.changeStatus(status);
    }

    public double setApprovedPopularity(final Long applyId, final double popularity) {
        ApplimentMember applimentMember = adminRepository.findApplimentMemberById(applyId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));
        applimentMember.setPopularity(popularity);
        return applimentMember.getPopularity();
    }

    private void validRecruitmentId(final Long recruitmentId) {
        adminRepository.findRecruitmentById(recruitmentId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
    }

    private void validNotLeader(final RecruitMember recruitMember) {
        if (recruitMember.getRecruitField().equals("LEADER")) throw new ApplimentStatusChangeException(APPLIMENT_STATUS_CHANGE_LEADER);
    }
}
