package com.moa.controller;

import com.moa.dto.ValueResponse;
import com.moa.dto.recruit.RecruitApplyRequest;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.RecruitMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruitment")
@RequiredArgsConstructor
public class RecruitMemberController {
    private final RecruitMemberService recruitMemberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{recruitmentId}/apply")
    public ValueResponse<String> applyToRecruit(@PathVariable Long recruitmentId, @RequestParam String position, @AuthenticationPrincipal JwtUser user) {
        RecruitApplyRequest request = new RecruitApplyRequest(recruitmentId, position, user.id());
        return new ValueResponse<>(recruitMemberService.applyMember(request));
    }

    @PostMapping("/{recruitmentId}/cancel")
    public ValueResponse<Long> cancelToRecruit(@PathVariable Long recruitmentId, @AuthenticationPrincipal JwtUser user) {
        return new ValueResponse<>(recruitMemberService.cancelRecruit(recruitmentId, user.id()));
    }
}
