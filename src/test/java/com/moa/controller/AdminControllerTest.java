package com.moa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.dto.member.ApprovedPopularityRequest;
import com.moa.global.config.WebBeanConfig;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.service.AdminService;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.moa.domain.member.ApprovalStatus.*;
import static com.moa.global.exception.ErrorCode.RECRUITMENT_NOT_FOUND;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AdminController.class)
@Import(WebBeanConfig.class)
class AdminControllerTest {
    @MockBean
    private AdminService adminService;
    @Autowired
    private ObjectMapper mapper;

    private MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext context,  RestDocumentationContextProvider restDocumentation) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("신청 멤버 조회 테스트")
    @Nested
    class ApplyInfo {

        @DisplayName("모든 신청 멤버를 조회한다.")
        @Test
        void applimentMemberInfo() throws Exception {
            //given
            List<ApplimentMemberResponse> memberResponses = new ArrayList<>();
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(1L)
                    .applyId(1L)
                    .status(PENDING)
                    .build());
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(2L)
                    .applyId(2L)
                    .status(REFUSE)
                    .build());
            given(adminService.getApplimentMembers(1L, null))
                    .willReturn(memberResponses);

            //when
            ResultActions actions = mvc.perform(
                    get("/recruitment/{recruitmentId}/apply/members", 1L)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            actions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.[0].userId").value("1"),
                    jsonPath("$.[0].status").value("대기중"),
                    jsonPath("$.[1].userId").value("2"),
                    jsonPath("$.[1].status").value("거절")
            ).andDo(print());

            verify(adminService).getApplimentMembers(1L, null);
        }

        @DisplayName("대기중인 신청 멤버를 조회한다.")
        @Test
        void applimentMemberInfo_Pending() throws Exception {
            //given
            List<ApplimentMemberResponse> memberResponses = new ArrayList<>();
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(1L)
                    .applyId(1L)
                    .status(PENDING)
                    .build());
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(2L)
                    .applyId(2L)
                    .status(PENDING)
                    .build());
            given(adminService.getApplimentMembers(1L, PENDING))
                    .willReturn(memberResponses);

            //when
            ResultActions actions = mvc.perform(
                    get("/recruitment/{recruitmentId}/apply/members", 1L)
                            .param("statusCode", "1")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            actions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.[0].userId").value("1"),
                    jsonPath("$.[0].status").value("대기중"),
                    jsonPath("$.[1].userId").value("2"),
                    jsonPath("$.[1].status").value("대기중")
            ).andDo(print());

            verify(adminService).getApplimentMembers(1L, PENDING);
        }

        @DisplayName("거절된 신청 멤버를 조회한다.")
        @Test
        void applimentMemberInfo_Refuse() throws Exception {
            //given
            List<ApplimentMemberResponse> memberResponses = new ArrayList<>();
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(1L)
                    .applyId(1L)
                    .status(REFUSE)
                    .build());
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(2L)
                    .applyId(2L)
                    .status(REFUSE)
                    .build());
            given(adminService.getApplimentMembers(1L, REFUSE))
                    .willReturn(memberResponses);

            //when
            ResultActions actions = mvc.perform(
                    get("/recruitment/{recruitmentId}/apply/members", 1L)
                            .param("statusCode", "3")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            actions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.[0].userId").value("1"),
                    jsonPath("$.[0].status").value("거절"),
                    jsonPath("$.[1].userId").value("2"),
                    jsonPath("$.[1].status").value("거절")
            ).andDo(print());

            verify(adminService).getApplimentMembers(1L, REFUSE);
        }

        @DisplayName("강퇴한 신청 멤버를 조회한다.")
        @Test
        void applimentMemberInfo_Kick() throws Exception {
            //given
            List<ApplimentMemberResponse> memberResponses = new ArrayList<>();
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(1L)
                    .applyId(1L)
                    .status(KICK)
                    .build());
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(2L)
                    .applyId(2L)
                    .status(KICK)
                    .build());
            given(adminService.getApplimentMembers(1L, KICK))
                    .willReturn(memberResponses);

            //when
            ResultActions actions = mvc.perform(
                    get("/recruitment/{recruitmentId}/apply/members", 1L)
                            .param("statusCode", "4")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            actions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.[0].userId").value("1"),
                    jsonPath("$.[0].status").value("강퇴"),
                    jsonPath("$.[1].userId").value("2"),
                    jsonPath("$.[1].status").value("강퇴")
            ).andDo(print());

            verify(adminService).getApplimentMembers(1L, KICK);
        }

        @DisplayName("잘못된 상태 코드를 입력하면 신청 멤버 조회에 실패한다")
        @Test
        void applimentMemberInfo_BadRequest() throws Exception {
            //given
            final String INVALID_CODE = "99";

            //when
            ResultActions actions = mvc.perform(
                    get("/recruitment/{recruitmentId}/apply/members", 1L)
                            .param("statusCode", INVALID_CODE)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            actions.andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.message").value("잘못된 상태 코드입니다."),
                    jsonPath("$.code").value("S0001")
            ).andDo(print());

            verify(adminService, times(0)).getApplimentMembers(anyLong(), any());
        }
    }

    @DisplayName("상태 변경 테스트")
    @Nested
    class ChangeStatus {
        @DisplayName("신청 멤버의 상태를 '승인'으로 변경한다.")
        @Test
        void changeStatusMember_Approved() throws Exception {
            //given
            given(adminService.changeApplimentStatus(1L, APPROVED))
                    .willReturn("APPROVED");

            //when
            ResultActions actions = mvc.perform(
                    post("/recruitment/{recruitmentId}/apply/{applyId}", 1L, 1L)
                            .param("statusCode", String.valueOf(APPROVED.getCode()))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            actions.andExpectAll(
                    status().isOk(),
                    content().string("APPROVED")
            ).andDo(print());

            verify(adminService).changeApplimentStatus(1L, APPROVED);
        }

        @DisplayName("신청 멤버의 상태를 '거절'로 변경한다.")
        @Test
        void changeStatusMember_Refuse() throws Exception {
            //given
            given(adminService.changeApplimentStatus(1L, REFUSE))
                    .willReturn("REFUSE");

            //when
            ResultActions actions = mvc.perform(
                    post("/recruitment/{recruitmentId}/apply/{applyId}", 1L, 1L)
                            .param("statusCode", String.valueOf(REFUSE.getCode()))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            actions.andExpectAll(
                    status().isOk(),
                    content().string("REFUSE")
            ).andDo(print());

            verify(adminService).changeApplimentStatus(1L, REFUSE);
        }

        @DisplayName("신청 멤버의 상태를 '강퇴'로 변경한다.")
        @Test
        void changeStatusMember_Kick() throws Exception {
            //given
            given(adminService.changeApplimentStatus(1L, KICK))
                    .willReturn("KICK");

            //when
            ResultActions actions = mvc.perform(
                    post("/recruitment/{recruitmentId}/apply/{applyId}", 1L, 1L)
                            .param("statusCode", String.valueOf(KICK.getCode()))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            actions.andExpectAll(
                    status().isOk(),
                    content().string("KICK")
            ).andDo(print());

            verify(adminService).changeApplimentStatus(1L, KICK);
        }

        @DisplayName("잘못된 상태 코드를 입력하면 상태 변경에 실패한다")
        @Test
        void changeStatusMember_Invalid() throws Exception {
            //given
            final String INVALID_CODE = "99";
            given(adminService.changeApplimentStatus(1L, KICK))
                    .willReturn("KICK");

            //when
            ResultActions actions = mvc.perform(
                    post("/recruitment/{recruitmentId}/apply/{applyId}", 1L, 1L)
                            .param("statusCode", INVALID_CODE)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
            );

            //then
            actions.andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.message").value("잘못된 상태 코드입니다."),
                    jsonPath("$.code").value("S0001")
            ).andDo(print());

            verify(adminService, times(0)).changeApplimentStatus(anyLong(), any());
        }
    }

    @DisplayName("승인된 멤버의 정보를 가져온다.")
    @Test
    void approvedMemberInfo() throws Exception {
        //given
        List<ApprovedMemberResponse> approvedMemberResponses = new ArrayList<>();
        ApprovedMemberResponse member1 = new ApprovedMemberResponse(1L, 1L, "hose", "백엔드", 3.5);
        ApprovedMemberResponse member2 = new ApprovedMemberResponse(2L, 2L, "sole", "백엔드", 3.5);
        approvedMemberResponses.add(member1);
        approvedMemberResponses.add(member2);

        given(adminService.getApprovedMembers(1L))
                .willReturn(approvedMemberResponses);

        //when
        ResultActions actions = mvc.perform(
                get("/recruitment/{recruitmentId}/approved/members", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        actions.andExpectAll(
                status().isOk(),
                jsonPath("$.[0].nickname").value("hose"),
                jsonPath("$.[1].nickname").value("sole")
        ).andDo(print());

        verify(adminService).getApprovedMembers(1L);
    }

    @DisplayName("승인된 멤버의 정보를 조회하는데 실패한다 [잘못된 모집글 아이디]")
    @Test
    void approvedMemberInfo_Invalid() throws Exception {
        //given
        final Long INVALID_ID = 99L;
        given(adminService.getApprovedMembers(INVALID_ID))
                .willThrow(new EntityNotFoundException(RECRUITMENT_NOT_FOUND));

        //when
        ResultActions actions = mvc.perform(
                get("/recruitment/{recruitmentId}/approved/members", INVALID_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        actions.andExpectAll(
                status().isNotFound(),
                jsonPath("$.message").value("해당 아이디를 가진 모집글을 찾을 수 없습니다"),
                jsonPath("$.code").value("R0001")
        ).andDo(print());
    }

    @DisplayName("인기도를 설정한다.")
    @Test
    void setPopularity() throws Exception {
        //given
        given(adminService.setApprovedPopularity(1L, 5.0))
                .willReturn(5.0);

        //when
        String json = mapper.writeValueAsString(new ApprovedPopularityRequest(5.0));
        ResultActions actions = mvc.perform(
                post("/recruitment/{recruitmentId}/approved/{applyId}/popularity", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        actions.andExpectAll(
                status().isOk(),
                content().string("5.0")
        ).andDo(print());

        verify(adminService).setApprovedPopularity(1L, 5.0);
    }

    @DisplayName("인기도 설정에 실패한다 (인기도가 5.0보다 큼)")
    @Test
    void setPopularity_invalidPopularityUp() throws Exception {
        //when
        String json = mapper.writeValueAsString(new ApprovedPopularityRequest(5.5));
        ResultActions actions = mvc.perform(
                post("/recruitment/{recruitmentId}/approved/{applyId}/popularity", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        actions.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").value("must be less than or equal to 5.0"),
                jsonPath("$.code").value("400")
        ).andDo(print());

        verify(adminService, times(0)).setApprovedPopularity(anyLong(), anyDouble());
    }

    @DisplayName("인기도 설정에 실패한다 (인기도가 0.5보다 작음)")
    @Test
    void setPopularity_invalidPopularityDown() throws Exception {
        //when
        String json = mapper.writeValueAsString(new ApprovedPopularityRequest(0.4));
        ResultActions actions = mvc.perform(
                post("/recruitment/{recruitmentId}/approved/{applyId}/popularity", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        actions.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").value("must be greater than or equal to 0.5"),
                jsonPath("$.code").value("400")
        ).andDo(print());

        verify(adminService, times(0)).setApprovedPopularity(anyLong(), anyDouble());
    }
}