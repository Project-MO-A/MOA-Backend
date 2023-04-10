package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.member.RecruitMemberRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.recruit.RecruitApplyRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.moa.domain.member.ApprovalStatus.PENDING;
import static com.moa.global.exception.ErrorCode.APPLIMENT_NOT_FOUND;
import static com.moa.global.exception.ErrorCode.RECRUITMEMEBER_NO_FIELD;

@Service
@Transactional
@RequiredArgsConstructor
public class RecruitMemberService {
    private final UserRepository userRepository;
    private final RecruitMemberRepository recruitMemberRepository;
    private final ApplimentMemberRepository applimentMemberRepository;

    public String applyMember(RecruitApplyRequest request) {
        RecruitMember recruitMember = recruitMemberRepository.findByRecruitFieldAndRecruitmentId(request.position(), request.recruitmentId())
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMEMEBER_NO_FIELD));
        User user = userRepository.getReferenceById(request.userId());
        return applimentMemberRepository.save(new ApplimentMember(recruitMember, user, PENDING)).getStatus().name();
    }

    public Long cancelRecruit(Long recruitmentId, Long userId) {
        ApplimentMember applimentMember = applimentMemberRepository.findByRecruitIdAndUserId(recruitmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));
        applimentMemberRepository.delete(applimentMember);
        return applimentMember.getId();
    }
}
