package com.moa.service;

import com.moa.domain.member.*;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.dto.member.ApprovedPopularityRequest;
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
    private final AdminSearchRepository adminRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ApplimentMemberResponse> getApplimentMembers(final Long recruitmentId, final ApprovalStatus status) {
        List<ApplimentMemberResponse> allApplimentResponse = adminRepository.findAllApplimentResponse(recruitmentId, status);
        if (allApplimentResponse.isEmpty()) throw new EntityNotFoundException(RECRUITMENT_NOT_FOUND);
        return allApplimentResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ApprovedMemberResponse> getApprovedMembers(final Long recruitmentId) {
        List<ApprovedMemberResponse> allApprovedResponse = adminRepository.findAllApprovedResponse(recruitmentId);
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

    public double setApprovedPopularity(final Long applyId, final ApprovedPopularityRequest popularityRequest) {
        ApplimentMember applimentMember = adminRepository.findApplimentMemberById(applyId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));
        applimentMember.setPopularity(popularityRequest.popularity());
        return applimentMember.getPopularity();
    }
}
