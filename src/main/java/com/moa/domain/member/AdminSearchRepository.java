package com.moa.domain.member;

import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;

import java.util.List;
import java.util.Optional;

public interface AdminSearchRepository {
    Optional<ApplimentMember> findApplimentMemberById(Long applyId);
    List<ApplimentMemberResponse> findAllApplimentResponse(final Long recruitmentId, final ApprovalStatus status);
    List<ApprovedMemberResponse> findAllApprovedResponse(final Long recruitmentId);
}
