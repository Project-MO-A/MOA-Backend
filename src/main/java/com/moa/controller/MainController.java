package com.moa.controller;

import com.moa.dto.ValueResponse;
import com.moa.dto.recruit.RecruitmentInfo;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class MainController {

    private final RecruitmentService recruitmentService;

    @GetMapping("/recruitment/popular")
    public ValueResponse<List<RecruitmentInfo>> getPopularRecruitment(){
        return new ValueResponse<>(recruitmentService.getTopThreeRecruitment());
    }

    @GetMapping("/recruitment/recommend")
    public ValueResponse<List<RecruitmentInfo>> recommendRecruitment(@AuthenticationPrincipal JwtUser user){
        return new ValueResponse<>(recruitmentService.getRecommendRecruitment(user.id()));
    }
}
