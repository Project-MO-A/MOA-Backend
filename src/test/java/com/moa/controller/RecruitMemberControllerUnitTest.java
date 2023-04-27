package com.moa.controller;

import com.moa.base.AbstractControllerUnitTest;
import com.moa.base.WithMockCustomUser;
import com.moa.dto.recruit.RecruitApplyRequest;
import com.moa.global.exception.GlobalControllerAdvice;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.service.RecruitMemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.moa.global.exception.ErrorCode.RECRUITMEMEBER_NO_FIELD;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({RecruitMemberController.class, GlobalControllerAdvice.class})
class RecruitMemberControllerUnitTest extends AbstractControllerUnitTest {

    @MockBean
    private RecruitMemberService recruitMemberService;

    private final Long recruitmentId = 1L;
    private final String position = "백엔드";
    private final Long userId = 1L;

    @Test
    @DisplayName("회원 참여 신청에 성공한다")
    @WithMockCustomUser
    void successApply() throws Exception {
        //given
        given(recruitMemberService.applyMember(new RecruitApplyRequest(recruitmentId, position, userId)))
                .willReturn("PENDING");
        //when
        ResultActions action = mvc.perform(post("/recruitment/{recruitmentId}/apply?position=" + position, recruitmentId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer Token"));

        //then
        action.andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value("PENDING"));
    }

    @Test
    @DisplayName("회원 참여 신청에 실패한다")
    @WithMockCustomUser
    void failApply() throws Exception {
        //given
        given(recruitMemberService.applyMember(new RecruitApplyRequest(recruitmentId, position, userId)))
                .willThrow(new EntityNotFoundException(RECRUITMEMEBER_NO_FIELD));

        //when
        ResultActions action = mvc.perform(post("/recruitment/{recruitmentId}/apply?position=" + position, recruitmentId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer Token"));

        //then
        action.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("R0002"))
                .andExpect(jsonPath("$.message").value("모집글에 일치하는 신청 분야가 없습니다"));
    }
}