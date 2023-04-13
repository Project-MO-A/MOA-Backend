package com.moa.service;

import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentSearchRepository;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.reply.ReplyRepository;
import com.moa.dto.page.PageResponse;
import com.moa.dto.page.SliceResponse;
import com.moa.dto.recruit.RecruitmentInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.moa.support.fixture.RecruitMemberFixture.BACKEND_MEMBER;
import static com.moa.support.fixture.RecruitmentFixture.PROGRAMMING_POST;
import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.UserFixture.PINGU;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecruitmentSearchServiceTest {

    @Mock
    private RecruitmentSearchRepository recruitmentSearchRepository;

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private RecruitmentSearchService searchService;

    @DisplayName("검색 조건과 일치하는 하나의 모집글이 조회된다.")
    @Test
    void searchOne() {
        //given
        Recruitment recruitment = PROGRAMMING_POST.생성(PINGU.생성(),
                BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                List.of(BACKEND_MEMBER.생성()));
        RecruitmentInfo recruitmentInfo = new RecruitmentInfo(recruitment);
        given(recruitmentSearchRepository.searchOne(anyMap()))
                .willReturn(recruitmentInfo);

        //when
        RecruitmentInfo info = searchService.searchOne(new ConcurrentHashMap<>());

        //then
        assertAll(
                () -> assertThat(info).isNotNull(),
                () -> verify(recruitmentSearchRepository).searchOne(anyMap())
        );
    }

    @DisplayName("검색 조건과 일치하는 여러 모집글을 조회한다. (페이징)")
    @Test
    void searchPageResponse() {
        //given
        List<RecruitmentInfo> content = List.of(new RecruitmentInfo(PROGRAMMING_POST.생성(PINGU.생성(),
                BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                List.of(BACKEND_MEMBER.생성()))));
        PageRequest pageRequest = PageRequest.of(1, 1);
        Page<RecruitmentInfo> recruitmentInfos = new PageImpl<>(content, pageRequest, 10);
        given(recruitmentSearchRepository.searchPage(anyMap(), any(Pageable.class)))
                .willReturn(recruitmentInfos);

        //when
        PageResponse<RecruitmentInfo> pageResponse = searchService.searchPageResponse(new ConcurrentHashMap<>(), pageRequest);

        //then
        assertAll(
                () -> assertThat(pageResponse.getTotalPage()).isEqualTo(10),
                () -> assertThat(pageResponse.getSize()).isEqualTo(1),
                () -> verify(recruitmentSearchRepository).searchPage(anyMap(), any())
        );
    }

    @DisplayName("검색 조건과 일치하는 여러 모집글을 조회한다.")
    @Test
    void searchSliceResponse() {
        //given
        List<RecruitmentInfo> content = List.of(new RecruitmentInfo(PROGRAMMING_POST.생성(PINGU.생성(),
                BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                List.of(BACKEND_MEMBER.생성()))));
        PageRequest pageRequest = PageRequest.of(0, 1);
        Slice<RecruitmentInfo> infos = new SliceImpl<>(content, pageRequest, true);
        given(recruitmentSearchRepository.searchSlice(anyMap(), any(Pageable.class)))
                .willReturn(infos);

        //when
        SliceResponse<RecruitmentInfo> sliceResponse = searchService.searchSliceResponse(new ConcurrentHashMap<>(), pageRequest);

        //then
        assertAll(
                () -> assertThat(sliceResponse.isLast()).isFalse(),
                () -> assertThat(sliceResponse.isFirst()).isTrue(),
                () -> assertThat(sliceResponse.getSize()).isEqualTo(1),
                () -> verify(recruitmentSearchRepository).searchSlice(anyMap(), any(PageRequest.class))
        );
    }

    @DisplayName("댓글 개수가 저장된다.")
    @Test
    void reply() {
        //given
        List<RecruitmentInfo> content = List.of(new RecruitmentInfo(PROGRAMMING_POST.생성(PINGU.생성(),
                BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                List.of(BACKEND_MEMBER.생성()))));
        PageRequest pageRequest = PageRequest.of(0, 1);
        Slice<RecruitmentInfo> infos = new SliceImpl<>(content, pageRequest, true);

        given(recruitmentSearchRepository.searchSlice(anyMap(), any(Pageable.class)))
                .willReturn(infos);
        given(replyRepository.countRepliesByRecruitmentId(any()))
                .willReturn(5);

        //when
        SliceResponse<RecruitmentInfo> sliceResponse = searchService.searchSliceResponse(new ConcurrentHashMap<>(), pageRequest);

        //then
        assertAll(
                () -> assertThat(sliceResponse.getContent().stream()
                        .filter(info -> info.getReplyCount() == 5)
                        .count()).isEqualTo(1),
                () -> verify(recruitmentSearchRepository).searchSlice(anyMap(), any(PageRequest.class))
        );
    }
}