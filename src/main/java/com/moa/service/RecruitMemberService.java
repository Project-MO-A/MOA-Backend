package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.member.RecruitMemberRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.recruit.RecruitApplyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.moa.domain.member.ApprovalStatus.PENDING;

@Service
@Transactional
@RequiredArgsConstructor
public class RecruitMemberService {
    private final UserRepository userRepository;
    private final RecruitMemberRepository recruitMemberRepository;
    private final ApplimentMemberRepository applimentMemberRepository;

    public String applyMember(RecruitApplyRequest request) {
        RecruitMember recruitMember = recruitMemberRepository.findByRecruitFieldAndRecruitmentId(request.position(), request.recruitmentId());
        User user = userRepository.getReferenceById(request.userId());
        return applimentMemberRepository.save(new ApplimentMember(recruitMember, user, PENDING)).getStatus().name();
    }
}
