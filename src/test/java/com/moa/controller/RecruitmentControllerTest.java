package com.moa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.base.WithMockCustomUser;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.dto.StatusResponse;
import com.moa.dto.member.RecruitMemberRequest;
import com.moa.dto.recruit.RecruitInfoResponse;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.recruit.RecruitUpdateRequest;
import com.moa.global.config.WebBeanConfig;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.global.exception.service.InvalidCodeException;
import com.moa.service.RecruitmentService;
import com.moa.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.moa.domain.recruit.RecruitStatus.CONCURRENT;
import static com.moa.global.exception.ErrorCode.RECRUITMENT_NOT_FOUND;
import static com.moa.global.exception.ErrorCode.STATUS_CODE_INVALID;
import static com.moa.support.fixture.RecruitMemberFixture.BACKEND_MEMBER;
import static com.moa.support.fixture.RecruitMemberFixture.FRONTEND_MEMBER;
import static com.moa.support.fixture.RecruitRequestFixture.ANOTHER_REQUEST;
import static com.moa.support.fixture.RecruitRequestFixture.BASIC_REQUEST;
import static com.moa.support.fixture.RecruitmentFixture.PROGRAMMING_POST;
import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.UserFixture.PINGU;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WithMockCustomUser
@Import(WebBeanConfig.class)
@WebMvcTest(RecruitmentController.class)
class RecruitmentControllerTest {
    @MockBean
    private RecruitmentService recruitmentService;
    @MockBean
    private TagService tagService;
    @Autowired
    private ObjectMapper mapper;
    private MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("모집글 테스트")
    @Nested
    class Post {
        @DisplayName("모집글을 등록한다.")
        @Test
        void post() throws Exception {
            //given
            RecruitPostRequest postRequest = BASIC_REQUEST.등록_생성();
            given(recruitmentService.post(anyLong(), any(), any()))
                    .willReturn(1L);

            //when
            ResultActions action = mvc.perform(
                    MockMvcRequestBuilders.post("/recruitment")
                            .contentType(APPLICATION_JSON)
                            .content(toJson(postRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isCreated(),
                    jsonPath("$.value").value("1")
            );

            assertAll(
                    () -> verify(tagService).updateAndReturn(anyList()),
                    () -> verify(recruitmentService).post(anyLong(), any(RecruitPostRequest.class), anyList())
            );
        }

        @DisplayName("제목을 입력하지 않으면 모집글 등록에 실패한다.")
        @Test
        void post_실패_제목_Null() throws Exception {
            //given
            RecruitPostRequest postRequest = BASIC_REQUEST.제목을_변경하여_등록_생성("");

            //when
            ResultActions action = mvc.perform(
                    MockMvcRequestBuilders.post("/recruitment")
                            .contentType(APPLICATION_JSON)
                            .content(toJson(postRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.code").value("400"),
                    jsonPath("$.message").value("must not be blank")
            );

            assertAll(
                    () -> verify(tagService, times(0)).updateAndReturn(anyList()),
                    () -> verify(recruitmentService, times(0)).post(anyLong(), any(RecruitPostRequest.class), anyList())
            );
        }

        @DisplayName("모집글 멤버가 없으면 모집글 등록에 실패한다.")
        @Test
        void post_실패_멤버_Empty() throws Exception {
            //given
            RecruitPostRequest postRequest = BASIC_REQUEST.멤버를_변경하여_등록_생성(new ArrayList<>());

            //when
            ResultActions action = mvc.perform(
                    MockMvcRequestBuilders.post("/recruitment")
                            .contentType(APPLICATION_JSON)
                            .content(toJson(postRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.code").value("400"),
                    jsonPath("$.message").value("must not be empty")
            );

            assertAll(
                    () -> verify(tagService, times(0)).updateAndReturn(anyList()),
                    () -> verify(recruitmentService, times(0)).post(anyLong(), any(RecruitPostRequest.class), anyList())
            );
        }

        @DisplayName("모집글 멤버에 필드가 없으면 모집글 등록에 실패한다.")
        @Test
        void post_실패_멤버_필드_Null() throws Exception {
            //given
            RecruitPostRequest postRequest = BASIC_REQUEST.멤버를_변경하여_등록_생성(List.of(
                    RecruitMemberRequest.builder()
                            .total(5)
                            .build())
            );

            //when
            ResultActions action = mvc.perform(
                    MockMvcRequestBuilders.post("/recruitment")
                            .contentType(APPLICATION_JSON)
                            .content(toJson(postRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.code").value("400"),
                    jsonPath("$.message").value("must not be blank")
            );

            assertAll(
                    () -> verify(tagService, times(0)).updateAndReturn(anyList()),
                    () -> verify(recruitmentService, times(0)).post(anyLong(), any(RecruitPostRequest.class), anyList())
            );
        }

        @DisplayName("모집글 멤버에 인원수가 없으면 모집글 등록에 실패한다.")
        @Test
        void post_실패_멤버_인원수_Null() throws Exception {
            //given
            RecruitPostRequest postRequest = BASIC_REQUEST.멤버를_변경하여_등록_생성(List.of(
                    RecruitMemberRequest.builder()
                            .field("백엔드")
                            .build())
            );

            //when
            ResultActions action = mvc.perform(
                    MockMvcRequestBuilders.post("/recruitment")
                            .contentType(APPLICATION_JSON)
                            .content(toJson(postRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.code").value("400"),
                    jsonPath("$.message").value("must be greater than 0")
            );

            assertAll(
                    () -> verify(tagService, times(0)).updateAndReturn(anyList()),
                    () -> verify(recruitmentService, times(0)).post(anyLong(), any(RecruitPostRequest.class), anyList())
            );
        }
    }

    @DisplayName("모집글을 조회한다.")
    @Test
    void info() throws Exception {
        //given
        RecruitInfoResponse response = new RecruitInfoResponse(
                PROGRAMMING_POST.생성(
                        PINGU.생성(),
                        BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                        List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성()))
        );
        given(recruitmentService.getInfo(1L))
                .willReturn(response);

        //when
        ResultActions action = mvc.perform(
                get("/recruitment/{recruitmentId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        action.andExpectAll(
                status().isOk(),
                jsonPath("$.title").value(PROGRAMMING_POST.getTitle()),
                jsonPath("$.postUser.userName").value(PINGU.getName()),
                jsonPath("$.members.[0].recruitField").value(BACKEND_MEMBER.getField())
        );

        verify(recruitmentService).getInfo(1L);
    }

    @DisplayName("잘못된 아이디를 입력하면 모집글 조회에 실패한다")
    @Test
    void info_잘못된_아이디() throws Exception {
        //given
        final Long invalidId = 99L;
        given(recruitmentService.getInfo(invalidId))
                .willThrow(new EntityNotFoundException(RECRUITMENT_NOT_FOUND));

        //when
        ResultActions action = mvc.perform(
                get("/recruitment/{recruitmentId}", invalidId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        action.andExpectAll(
                status().isNotFound(),
                jsonPath("$.code").value("R0001")
        );

        verify(recruitmentService).getInfo(invalidId);
    }

    @DisplayName("모집글 수정 테스트")
    @Nested
    class Update {
        @DisplayName("모집글을 수정한다.")
        @Test
        void update() throws Exception {
            //given
            RecruitUpdateRequest updateRequest = ANOTHER_REQUEST.수정_생성();
            given(recruitmentService.update(anyLong(), any(), any()))
                    .willReturn(1L);

            //when
            ResultActions action = mvc.perform(
                    patch("/recruitment/{recruitmentId}", 1L)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(updateRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isOk(),
                    jsonPath("$.value").value("1")
            );

            assertAll(
                    () -> verify(tagService).updateAndReturn(any()),
                    () -> verify(recruitmentService).update(anyLong(), any(RecruitUpdateRequest.class), anyList())
            );
        }

        @DisplayName("모집글 수정한다. (제목)")
        @Test
        void update_제목() throws Exception {
            //given
            RecruitUpdateRequest updateRequest = RecruitUpdateRequest.builder()
                    .title("수정된 제목")
                    .build();
            given(recruitmentService.update(anyLong(), any(), any()))
                    .willReturn(1L);

            //when
            ResultActions action = mvc.perform(
                    patch("/recruitment/{recruitmentId}", 1L)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(updateRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isOk(),
                    jsonPath("$.value").value("1")
            );

            assertAll(
                    () -> verify(tagService).updateAndReturn(any()),
                    () -> verify(recruitmentService).update(anyLong(), any(RecruitUpdateRequest.class), any())
            );
        }

        @DisplayName("모집글 수정한다. (멤버)")
        @Test
        void update_멤버() throws Exception {
            //given
            RecruitUpdateRequest updateRequest = RecruitUpdateRequest.builder()
                    .memberFields(List.of(
                            RecruitMemberRequest.builder().field("백엔드").total(5).build(),
                            RecruitMemberRequest.builder().field("프론트엔드").total(3).build()
                    ))
                    .build();
            given(recruitmentService.update(anyLong(), any(), any()))
                    .willReturn(1L);

            //when
            ResultActions action = mvc.perform(
                    patch("/recruitment/{recruitmentId}", 1L)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(updateRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isOk(),
                    jsonPath("$.value").value("1")
            );

            assertAll(
                    () -> verify(tagService).updateAndReturn(any()),
                    () -> verify(recruitmentService).update(anyLong(), any(RecruitUpdateRequest.class), any())
            );
        }

        @DisplayName("모집글 수정하는데 실패한다. (멤버 필드가 비어있음)")
        @Test
        void update_실패_멤버_필드() throws Exception {
            //given
            RecruitUpdateRequest updateRequest = RecruitUpdateRequest.builder()
                    .memberFields(List.of(
                            RecruitMemberRequest.builder().total(5).build(),
                            RecruitMemberRequest.builder().field("프론트엔드").total(3).build()
                    ))
                    .build();
            given(recruitmentService.update(anyLong(), any(), any()))
                    .willReturn(1L);

            //when
            ResultActions action = mvc.perform(
                    patch("/recruitment/{recruitmentId}", 1L)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(updateRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.code").value("400"),
                    jsonPath("$.message").value("must not be blank")
            );

            assertAll(
                    () -> verify(tagService, times(0)).updateAndReturn(any()),
                    () -> verify(recruitmentService, times(0)).update(anyLong(), any(RecruitUpdateRequest.class), any())
            );
        }

        @DisplayName("모집글 수정하는데 실패한다. (멤버 인원이 null)")
        @Test
        void update_실패_멤버_인원() throws Exception {
            //given
            RecruitUpdateRequest updateRequest = RecruitUpdateRequest.builder()
                    .memberFields(List.of(
                            RecruitMemberRequest.builder().field("백엔드").build(),
                            RecruitMemberRequest.builder().field("프론트엔드").total(3).build()
                    ))
                    .build();
            given(recruitmentService.update(anyLong(), any(), any()))
                    .willReturn(1L);

            //when
            ResultActions action = mvc.perform(
                    patch("/recruitment/{recruitmentId}", 1L)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(updateRequest))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.code").value("400"),
                    jsonPath("$.message").value("must be greater than 0")
            );

            assertAll(
                    () -> verify(tagService, times(0)).updateAndReturn(any()),
                    () -> verify(recruitmentService, times(0)).update(anyLong(), any(RecruitUpdateRequest.class), any())
            );
        }

        @DisplayName("모집글을 상태를 변경한다.")
        @Test
        void updateStatus() throws Exception {
            //given
            String name = CONCURRENT.name();
            given(recruitmentService.updateStatus(1L, 2))
                    .willReturn(new StatusResponse(name));

            //when
            ResultActions action = mvc.perform(
                    post("/recruitment/{recruitmentId}", 1L)
                            .param("status", "2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("CONCURRENT")
            );

            verify(recruitmentService).updateStatus(1L, 2);
        }

        @DisplayName("잘못된 코드를 입력하면 모집글 상태를 변경하는데 실패한다")
        @Test
        void updateStatus_잘못된_코드() throws Exception {
            //given
            final int INVALID_CODE = 99;
            given(recruitmentService.updateStatus(1L, INVALID_CODE))
                    .willThrow(new InvalidCodeException(STATUS_CODE_INVALID));

            //when
            ResultActions action = mvc.perform(
                    post("/recruitment/{recruitmentId}", 1L)
                            .param("status", "99")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            action.andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.code").value("S0001"),
                    jsonPath("$.message").value("잘못된 상태 코드입니다.")
            );

            verify(recruitmentService).updateStatus(1L, 99);
        }
    }

    @DisplayName("모집글을 삭제한다")
    @Test
    void delete() throws Exception {
        //given
        given(recruitmentService.delete(1L))
                .willReturn(1L);

        //when
        ResultActions action = mvc.perform(
                MockMvcRequestBuilders.delete("/recruitment/{recruitmentId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        action.andExpectAll(
                status().isOk(),
                jsonPath("$.value").value(1)
        );
        verify(recruitmentService).delete(1L);
    }

    private String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}