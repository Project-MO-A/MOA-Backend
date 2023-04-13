package com.moa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.dto.page.PageResponse;
import com.moa.dto.page.SliceResponse;
import com.moa.dto.recruit.RecruitmentInfo;
import com.moa.global.config.WebBeanConfig;
import com.moa.service.RecruitmentSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.moa.domain.base.SearchParam.STATE_CODE;
import static com.moa.support.fixture.RecruitMemberFixture.BACKEND_MEMBER;
import static com.moa.support.fixture.RecruitmentFixture.PROGRAMMING_POST;
import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.UserFixture.PINGU;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(RecruitmentSearchController.class)
@Import(WebBeanConfig.class)
class RecruitmentSearchControllerTest {
    @MockBean
    private RecruitmentSearchService searchService;
    @Autowired
    private ObjectMapper mapper;

    private MockMvc mvc;

    private List<RecruitmentInfo> CONTENTS;

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        CONTENTS = List.of(
                new RecruitmentInfo(PROGRAMMING_POST.생성(PINGU.생성(),
                        BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                        List.of(BACKEND_MEMBER.생성()))),
                new RecruitmentInfo(PROGRAMMING_POST.생성(PINGU.생성(),
                        BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                        List.of(BACKEND_MEMBER.생성())))
        );
    }



    @DisplayName("응답 데이터에 페이지 정보가 포함된다.")
    @Test
    void searchRecruitmentsPage() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(1, 5);
        Page<RecruitmentInfo> recruitmentInfos = new PageImpl<>(CONTENTS, pageRequest, 11);
        PageResponse<RecruitmentInfo> response = new PageResponse<>(recruitmentInfos);

        given(searchService.searchPageResponse(anyMap(), any(Pageable.class)))
                .willReturn(response);

        //when
        ResultActions actions = mvc.perform(
                get("/recruitment/search/page")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
                        .param("page", "2")
                        .param("sort", "createdDate,desc")
                        .param("size", "5")
                        .param(STATE_CODE.getParamKey(), "1")
        );

        //then
        actions.andExpectAll(
                status().isOk(),
                jsonPath("$.totalElement").value(11),
                jsonPath("$.totalPage").value(3)
        ).andDo(print());

        verify(searchService).searchPageResponse(anyMap(), any(Pageable.class));
    }

    @DisplayName("응답 데이터에 슬라이스 정보가 포함된다.")
    @Test
    void searchRecruitmentSlice() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 5);
        Slice<RecruitmentInfo> recruitmentInfos = new SliceImpl<>(CONTENTS, pageRequest, true);
        SliceResponse<RecruitmentInfo> response = new SliceResponse<>(recruitmentInfos);

        given(searchService.searchSliceResponse(anyMap(), any(Pageable.class)))
                .willReturn(response);

        //when
        ResultActions actions = mvc.perform(
                get("/recruitment/search/slice")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
                        .param("page", "2")
                        .param("sort", "createdDate,desc")
                        .param("size", "5")
                        .param(STATE_CODE.getParamKey(), "1")
        );

        //then
        actions.andExpectAll(
                status().isOk(),
                jsonPath("$.first").value("true"),
                jsonPath("$.last").value("false"),
                jsonPath("$.currentPage").value(1)
        ).andDo(print());

        verify(searchService).searchSliceResponse(anyMap(), any(Pageable.class));
    }
}