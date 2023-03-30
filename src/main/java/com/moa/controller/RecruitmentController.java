package com.moa.controller;

import com.moa.dto.StatusResponse;
import com.moa.dto.recruit.RecruitInfoResponse;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.recruit.RecruitUpdateRequest;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.TagService;
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
    private final RecruitmentService recruitmentService;
    private final TagService tagService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Long post(@RequestBody @Valid RecruitPostRequest request, @AuthenticationPrincipal JwtUser user) {
        List<Long> tagId = tagService.updateAndReturnId(request.tags()).orElse(new ArrayList<>());
        return recruitmentService.post(user.id(), request, tagId);
    }

    @GetMapping("/{recruitmentId}")
    public RecruitInfoResponse info(@PathVariable Long recruitmentId) {
        return recruitmentService.getInfo(recruitmentId);
    }

    @PatchMapping("/{recruitmentId}")
    public Long updatePost(@PathVariable Long recruitmentId, @RequestBody @Valid RecruitUpdateRequest request) {
        List<Long> tagId = tagService.updateAndReturnId(request.tags()).orElse(new ArrayList<>());
        return recruitmentService.update(recruitmentId, request, tagId);
    }

    @PostMapping("/{recruitmentId}")
    public StatusResponse updateStatus(@PathVariable Long recruitmentId, @RequestParam Integer status) {
        return recruitmentService.updateStatus(recruitmentId, status);
    }

    @DeleteMapping("/{recruitmentId}")
    public Long delete(@PathVariable Long recruitmentId) {
        return recruitmentService.delete(recruitmentId);
    }

}
