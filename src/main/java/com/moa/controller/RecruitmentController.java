package com.moa.controller;

import com.moa.domain.recruit.RecruitStatus;
import com.moa.dto.StatusResponse;
import com.moa.dto.recruit.RecruitApplyRequest;
import com.moa.dto.recruit.RecruitInfoResponse;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.recruit.RecruitUpdateRequest;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.CategoryService;
import com.moa.service.RecruitMemberService;
import com.moa.service.RecruitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {
    private final RecruitMemberService recruitMemberService;
    private final RecruitmentService recruitmentService;
    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Long post(@RequestBody @Valid RecruitPostRequest request, @AuthenticationPrincipal JwtUser user) {
        List<Long> categoryId = categoryService.updateAndReturnId(request.category()).orElse(new ArrayList<>());
        return recruitmentService.post(user.id(), request, categoryId);
    }

    @GetMapping("/{recruitmentId}")
    public RecruitInfoResponse info(@PathVariable Long recruitmentId) {
        return recruitmentService.getInfo(recruitmentId);
    }

    @PatchMapping("/{recruitmentId}")
    public Long updatePost(@PathVariable Long recruitmentId, @RequestBody @Valid RecruitUpdateRequest request) {
        List<Long> categoryId = categoryService.updateAndReturnId(request.category()).orElse(new ArrayList<>());
        return recruitmentService.update(recruitmentId, request, categoryId);
    }

    @PostMapping("/{recruitmentId}")
    public StatusResponse updateStatus(@PathVariable Long recruitmentId, @RequestParam Integer status) {
        return recruitmentService.updateStatus(recruitmentId, status);
    }

    @DeleteMapping("/{recruitmentId}")
    public Long delete(@PathVariable Long recruitmentId) {
        return recruitmentService.delete(recruitmentId);
    }
    
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{recruitmentId}/apply")
    public StatusResponse applyToRecruit(@PathVariable Long recruitmentId, @RequestParam String position, @AuthenticationPrincipal JwtUser user) {
        RecruitApplyRequest request = new RecruitApplyRequest(recruitmentId, position, user.id());
        return new StatusResponse(recruitMemberService.applyMember(request));
    }
}
