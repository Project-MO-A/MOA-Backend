package com.moa.domain.member;

import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;

import java.util.List;
import java.util.Optional;

public interface ApplimentSearchRepository {
    Optional<ApplimentMember> findApplimentMemberById(Long applyId);
    List<ApplimentMemberResponse> findAllApplimentMembers(final Long recruitmentId, final ApprovalStatus status);
    List<ApprovedMemberResponse> findAllApprovedMembers(final Long recruitmentId);
}
