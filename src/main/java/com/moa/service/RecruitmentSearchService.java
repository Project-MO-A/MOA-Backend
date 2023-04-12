package com.moa.service;

import com.moa.domain.recruit.RecruitmentSearchRepository;
import com.moa.dto.page.PageResponse;
import com.moa.dto.page.SliceResponse;
import com.moa.dto.recruit.RecruitmentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecruitmentSearchService {
    private final RecruitmentSearchRepository recruitmentSearchRepository;

    public RecruitmentInfo searchOne(Map<String, String> searchCondition) {
        return recruitmentSearchRepository.searchOne(searchCondition);
    }

    public PageResponse<RecruitmentInfo> searchPageResponse(Map<String, String> searchCondition, Pageable pageable) {
        Page<RecruitmentInfo> recruitmentInfoPage = recruitmentSearchRepository.searchPage(searchCondition, pageable);
        return new PageResponse<>(recruitmentInfoPage);
    }

    public SliceResponse<RecruitmentInfo> searchSliceResponse(Map<String, String> searchCondition, Pageable pageable) {
        Slice<RecruitmentInfo> recruitmentInfoSlice = recruitmentSearchRepository.searchSlice(searchCondition, pageable);
        return new SliceResponse<>(recruitmentInfoSlice);
    }
}
