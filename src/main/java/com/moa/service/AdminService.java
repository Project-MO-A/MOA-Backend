package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentSearchRepository;
import com.moa.domain.member.ApprovalStatus;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.global.exception.service.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.moa.domain.member.ApprovalStatus.*;
import static com.moa.global.exception.ErrorCode.APPLIMENT_NOT_FOUND;
import static com.moa.global.exception.ErrorCode.RECRUITMENT_NOT_FOUND;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminService implements ApplimentHandler {
    private final ApplimentSearchRepository adminRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ApplimentMemberResponse> getApplimentMembers(final Long recruitmentId, final ApprovalStatus status) {
        List<ApplimentMemberResponse> allApplimentResponse = adminRepository.findAllApplimentMembers(recruitmentId, status);
        if (allApplimentResponse.isEmpty()) throw new EntityNotFoundException(RECRUITMENT_NOT_FOUND);
        return allApplimentResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ApprovedMemberResponse> getApprovedMembers(final Long recruitmentId) {
        List<ApprovedMemberResponse> allApprovedResponse = adminRepository.findAllApprovedMembers(recruitmentId);
        if (allApprovedResponse.isEmpty()) throw new EntityNotFoundException(RECRUITMENT_NOT_FOUND);
        return allApprovedResponse;
    }

    @Override
    public String changeApplimentStatus(final Long applyId, final ApprovalStatus status) {
        ApplimentMember applimentMember = adminRepository.findApplimentMemberById(applyId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));

        if (status == APPROVED) applimentMember.getRecruitMember().addCount();
        else if (status == REFUSE || status == KICK) applimentMember.getRecruitMember().minusCount();
        return applimentMember.changeStatus(status);
    }

    public double setApprovedPopularity(final Long applyId, final double popularity) {
        ApplimentMember applimentMember = adminRepository.findApplimentMemberById(applyId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));
        applimentMember.setPopularity(popularity);
        return applimentMember.getPopularity();
    }
}
