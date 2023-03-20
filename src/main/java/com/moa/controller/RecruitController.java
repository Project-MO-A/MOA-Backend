package com.moa.controller;

import com.moa.dto.recruit.RecruitInfoResponse;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.recruit.RecruitUpdateRequest;
import com.moa.service.CategoryService;
import com.moa.service.RecruitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/recruit")
@RequiredArgsConstructor
@RestController
public class RecruitController {
    private final RecruitService recruitService;
    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Long post(@RequestBody @Valid RecruitPostRequest request, Long userId) {
        List<Long> categoryId = categoryService.updateAndReturnId(request.category()).orElse(new ArrayList<>());
        return recruitService.post(userId, request, categoryId);
    }

    @GetMapping("/{recruitId}")
    public RecruitInfoResponse info(@PathVariable Long recruitId) {
        return recruitService.getInfo(recruitId);
    }

    @PatchMapping("/{recruitId}")
    public Long updatePost(@PathVariable Long recruitId, @RequestBody @Valid RecruitUpdateRequest request) {
        List<Long> categoryId = categoryService.updateAndReturnId(request.category()).orElse(new ArrayList<>());
        return recruitService.update(recruitId, request, categoryId);
    }

    @PostMapping("/{recruitId}")
    public Long updateStatus(@PathVariable Long recruitId, @RequestParam Integer statusCode) {
        return recruitService.updateStatus(recruitId, statusCode);
    }

    @DeleteMapping("/{recruitId}")
    public Long delete(@PathVariable Long recruitId) {
        return recruitService.delete(recruitId);
    }
}
