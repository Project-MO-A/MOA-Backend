package com.moa.controller;

import com.moa.domain.recruit.tag.Tag;
import com.moa.dto.StatusResponse;
import com.moa.dto.ValueResponse;
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
    public ValueResponse<Long> post(@RequestBody @Valid RecruitPostRequest request, @AuthenticationPrincipal JwtUser user) {
        List<Tag> tags = tagService.updateAndReturn(request.tags()).orElse(new ArrayList<>());
        Long postId = recruitmentService.post(user.id(), request, tags);
        return new ValueResponse<>(postId);
    }

    @GetMapping("/{recruitmentId}")
    public RecruitInfoResponse info(@PathVariable Long recruitmentId) {
        return recruitmentService.getInfo(recruitmentId);
    }

    @PatchMapping("/{recruitmentId}")
    public ValueResponse<Long> update(@PathVariable Long recruitmentId, @RequestBody @Valid RecruitUpdateRequest request) {
        List<Tag> tags = tagService.updateAndReturn(request.tags()).orElse(new ArrayList<>());
        Long updatePostId = recruitmentService.update(recruitmentId, request, tags);
        return new ValueResponse<>(updatePostId);
    }

    @PostMapping("/{recruitmentId}")
    public StatusResponse updateStatus(@PathVariable Long recruitmentId, @RequestParam Integer status) {
        return recruitmentService.updateStatus(recruitmentId, status);
    }

    @DeleteMapping("/{recruitmentId}")
    public ValueResponse<Long> delete(@PathVariable Long recruitmentId) {
        Long deletePostId = recruitmentService.delete(recruitmentId);
        return new ValueResponse<>(deletePostId);
    }

    @PostMapping("/{recruitmentId}/concern")
    public ValueResponse<Long> concernRecruitment(@PathVariable Long recruitmentId, @AuthenticationPrincipal JwtUser user){
        return new ValueResponse<>(recruitmentService.concern(recruitmentId, user.id()));
    }
}
