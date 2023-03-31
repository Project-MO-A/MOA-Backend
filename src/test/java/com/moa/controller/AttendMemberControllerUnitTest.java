package com.moa.controller;

import com.moa.base.WithMockCustomUser;
import com.moa.dto.notice.VoteAttendanceRequest;
import com.moa.global.config.WebBeanConfig;
import com.moa.service.AttendMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AttendMemberController.class)
@Import(WebBeanConfig.class)
class AttendMemberControllerUnitTest {

    @MockBean
    private AttendMemberService attendMemberService;

    private MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("공지사항에 참석을 선택한다")
    @WithMockCustomUser
    void voteAttend() throws Exception {
        //given
        VoteAttendanceRequest request = VoteAttendanceRequest.builder()
                .recruitmentId(1L)
                .noticeId(1L)
                .attendance("ATTENDANCE")
                .userId(1L)
                .build();

        given(attendMemberService.voteAttendance(request)).willReturn(1L);

        //when
        ResultActions action = mvc.perform(post("/recruitment/{recruitmentId}/notice/{noticeId}/vote/{attendance}", 1L, 1L, "ATTENDANCE")
                .header(HttpHeaders.AUTHORIZATION, "Bearer Token"));

        //then
        action.andExpect(status().isCreated())
                .andDo(print());
    }
}