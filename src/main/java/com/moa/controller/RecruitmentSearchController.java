package com.moa.controller;

import com.moa.dto.page.PageResponse;
import com.moa.dto.page.SliceResponse;
import com.moa.dto.recruit.RecruitmentInfo;
import com.moa.service.RecruitmentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@RequestMapping("/recruitment/search")
@RestController
public class RecruitmentSearchController {
    private final RecruitmentSearchService recruitmentSearchService;

    @GetMapping("/one")
    public RecruitmentInfo searchRecruitment(@RequestParam Map<String, String> searchCondition) {
        return recruitmentSearchService.searchOne(searchCondition);
    }

    @GetMapping("/page")
    public PageResponse<RecruitmentInfo> searchRecruitmentsPage(@RequestParam Map<String, String> searchCondition,
                                                                @PageableDefault(sort = "createdDate", direction = DESC) Pageable pageable) {
        return recruitmentSearchService.searchPageResponse(searchCondition, pageable);
    }

    @GetMapping("/slice")
    public SliceResponse<RecruitmentInfo> searchRecruitmentSlice(@RequestParam Map<String, String> searchCondition,
                                                                 @PageableDefault(sort = "createdDate", direction = DESC) Pageable pageable) {
        return recruitmentSearchService.searchSliceResponse(searchCondition, pageable);
    }
}
