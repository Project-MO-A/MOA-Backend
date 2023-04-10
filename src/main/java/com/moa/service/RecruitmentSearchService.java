package com.moa.service;

import com.moa.domain.recruit.RecruitmentSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class RecruitmentSearchService {
    private final RecruitmentSearchRepository recruitmentSearchRepository;


}
