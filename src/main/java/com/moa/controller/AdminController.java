package com.moa.controller;

import com.moa.domain.member.ApprovalStatus;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.dto.member.ApprovedPopularityRequest;
import com.moa.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.moa.domain.member.ApprovalStatus.*;

@RequestMapping("/recruitment/{recruitmentId}")
@RequiredArgsConstructor
@Controller
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/apply/members")
    public List<ApplimentMemberResponse> applimentMemberInfo (@PathVariable Long recruitmentId, @RequestParam int statusCode) {
        return adminService.getApplimentMembers(recruitmentId, ApprovalStatus.getStatus(statusCode));
    }

    @PostMapping("/apply/{applyId}")
    public String changeStatusMember(@PathVariable Long applyId, @RequestParam int statusCode) {
        ApprovalStatus status = getStatus(statusCode);
        return adminService.changeApplimentStatus(applyId, status);
    }

    @GetMapping("/approved/members")
    public List<ApprovedMemberResponse> approvedMemberInfo (@PathVariable Long recruitmentId) {
        return adminService.getApprovedMembers(recruitmentId);
    }

    @PostMapping("/approved/{applyId}/popularity")
    public double setPopularity(@PathVariable Long applyId, @RequestBody @Valid ApprovedPopularityRequest popularityRequest) {
        return adminService.setApprovedPopularity(applyId, popularityRequest);
    }
}