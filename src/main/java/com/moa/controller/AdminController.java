package com.moa.controller;

import com.moa.domain.member.ApprovalStatus;
import com.moa.dto.ValueResponse;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.dto.member.ApprovedPopularityRequest;
import com.moa.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.moa.domain.member.ApprovalStatus.getStatus;

@RequestMapping("/recruitment/{recruitmentId}")
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/apply/members")
    public List<ApplimentMemberResponse> applimentMemberInfo (@PathVariable Long recruitmentId, @RequestParam(required = false) Integer statusCode) {
        ApprovalStatus status = statusCode != null ? ApprovalStatus.getStatus(statusCode) : null;
        return adminService.getApplimentMembers(recruitmentId, status);
    }

    @PostMapping("/apply/{applyId}")
    public ValueResponse<String> changeStatusMember(@PathVariable Long applyId, @RequestParam int statusCode) {
        ApprovalStatus status = getStatus(statusCode);
        String statusName = adminService.changeApplimentStatus(applyId, status);
        return new ValueResponse<>(statusName);
    }

    @GetMapping("/approved/members")
    public List<ApprovedMemberResponse> approvedMemberInfo (@PathVariable Long recruitmentId) {
        return adminService.getApprovedMembers(recruitmentId);
    }

    @PostMapping("/approved/{applyId}/popularity")
    public ValueResponse<Double> setPopularity(@PathVariable Long applyId, @RequestBody @Valid ApprovedPopularityRequest popularityRequest) {
        double popularity = adminService.setApprovedPopularity(applyId, popularityRequest.popularity());
        return new ValueResponse<>(popularity);
    }
}