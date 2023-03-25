package com.moa.service;

import com.moa.domain.member.AdminRepository;
import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.member.ApprovalStatus;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.dto.member.ApplimentMemberResponse;
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
public class AdminService {
    private final ApplimentMemberRepository applimentRepository;
    private final AdminRepository adminRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Transactional(readOnly = true)
    public List<ApplimentMemberResponse> getApplimentMembers(final Long recruitmentId, final ApprovalStatus status) {
        List<ApplimentMemberResponse> allApplimentResponse = adminRepository.findAllApplimentResponse(recruitmentId, status);
        if (allApplimentResponse.isEmpty()) throw new EntityNotFoundException(RECRUITMENT_NOT_FOUND);
        return allApplimentResponse;
    }

    public String changeApplimentStatus(final Long applyId, final ApprovalStatus status) {
        ApplimentMember applimentMember = applimentRepository.findFetchById(applyId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));

        if (status == APPROVED) applimentMember.getRecruitMember().addCount();
        else if (status == REFUSE || status == KICK) applimentMember.getRecruitMember().minusCount();
        return applimentMember.changeStatus(status);
    }
}
