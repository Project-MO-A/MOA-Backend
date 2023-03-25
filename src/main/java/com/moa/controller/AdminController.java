package com.moa.controller;

import com.moa.domain.member.ApprovalStatus;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.service.AdminService;
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
}