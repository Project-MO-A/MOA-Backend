package com.moa.service;

import com.moa.domain.member.ApprovalStatus;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;

import java.util.List;

public interface ApplimentHandler {
    List<ApplimentMemberResponse> getApplimentMembers(Long recruitmentId, ApprovalStatus status);
    List<ApprovedMemberResponse> getApprovedMembers(Long recruitmentId);
    String changeApplimentStatus(Long applyId, ApprovalStatus status);
}
