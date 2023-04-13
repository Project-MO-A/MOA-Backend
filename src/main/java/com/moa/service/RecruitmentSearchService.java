package com.moa.service;

import com.moa.domain.recruit.RecruitmentSearchRepository;
import com.moa.domain.reply.ReplyRepository;
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
    private final ReplyRepository replyRepository;

    public RecruitmentInfo searchOne(Map<String, String> searchCondition) {
        RecruitmentInfo info = recruitmentSearchRepository.searchOne(searchCondition);
        setReplyCount(info);
        return info;
    }

    public PageResponse<RecruitmentInfo> searchPageResponse(Map<String, String> searchCondition, Pageable pageable) {
        Page<RecruitmentInfo> recruitmentInfoPage = recruitmentSearchRepository.searchPage(searchCondition, pageable);
        setReplyCount(recruitmentInfoPage);
        return new PageResponse<>(recruitmentInfoPage);
    }

    public SliceResponse<RecruitmentInfo> searchSliceResponse(Map<String, String> searchCondition, Pageable pageable) {
        Slice<RecruitmentInfo> recruitmentInfoSlice = recruitmentSearchRepository.searchSlice(searchCondition, pageable);
        setReplyCount(recruitmentInfoSlice);
        return new SliceResponse<>(recruitmentInfoSlice);
    }

    private void setReplyCount(RecruitmentInfo recruitmentInfo) {
        final Long recruitmentId = recruitmentInfo.getId();
        int replyCount = replyRepository.countRepliesByRecruitmentId(recruitmentId);
        recruitmentInfo.setReplyCount(replyCount);
    }

    private void setReplyCount(Slice<RecruitmentInfo> recruitmentInfos) {
        for (RecruitmentInfo recruitmentInfo : recruitmentInfos) {
            setReplyCount(recruitmentInfo);
        }
    }
}
