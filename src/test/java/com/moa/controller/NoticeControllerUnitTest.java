package com.moa.controller;

import com.moa.base.AbstractControllerUnitTest;
import com.moa.dto.notice.NoticesResponse;
import com.moa.dto.notice.NoticesResponse.NoticeResponse;
import com.moa.dto.notice.NoticesResponse.NoticeResponse.Member;
import com.moa.dto.notice.PostNoticeRequest;
import com.moa.dto.notice.UpdateNoticeRequest;
import com.moa.global.exception.GlobalControllerAdvice;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static com.moa.global.exception.ErrorCode.NOTICE_NOT_FOUND;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({NoticeController.class, GlobalControllerAdvice.class})
class NoticeControllerUnitTest extends AbstractControllerUnitTest {

    @MockBean
    private NoticeService noticeService;

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("공지사항 등록에 성공한다")
    void successPostNotice() throws Exception {
        //given
        PostNoticeRequest request = new PostNoticeRequest("content", true);
        given(noticeService.post(1L, request)).willReturn(1L);

        //when
        ResultActions action = mvc.perform(post("/recruitment/{recruitmentId}/notice", 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(1));
    }

    @Test
    @DisplayName("공지사항 업데이트에 성공한다")
    void successUpdateNotice() throws Exception{
        //given
        UpdateNoticeRequest request = new UpdateNoticeRequest( null, true);
        given(noticeService.update(1L, 1L, request)).willReturn(1L);

        //when
        ResultActions action = mvc.perform(patch("/recruitment/{recruitmentId}/notice/{noticeId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(1L));
    }

    @Test
    @DisplayName("잘못된 공지사항 아이디 입력으로 인한 공지사항 업데이트에 실패한다")
    void failUpdateNotice() throws Exception {
        //given
        UpdateNoticeRequest request = new UpdateNoticeRequest(null, true);
        given(noticeService.update(1L, 0L, request))
                .willThrow(new EntityNotFoundException(NOTICE_NOT_FOUND));

        //when
        ResultActions action = mvc.perform(patch("/recruitment/{recruitmentId}/notice/{noticeId}", 1L, 0L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("N0001"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 공지사항입니다"));
    }

    @Test
    @DisplayName("공지사항 삭제에 성공한다")
    void successDeleteNotice() throws Exception {
        //given
        given(noticeService.delete(1L, 1L)).willReturn(1L);

        //when
        ResultActions action = mvc.perform(delete("/recruitment/{recruitmentId}/notice/{noticeId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"));

        //then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(1L));
    }

    @Test
    @DisplayName("잘못된 공지사항 아이디 입력으로 인한 공지사항 삭제에 실패한다")
    void failDeleteNotice() throws Exception {
        //given
        given(noticeService.delete(1L, 0L))
                .willThrow(new EntityNotFoundException(NOTICE_NOT_FOUND));

        //when
        ResultActions action = mvc.perform(delete("/recruitment/{recruitmentId}/notice/{noticeId}", 1L, 0L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"));

        //then
        action.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("N0001"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 공지사항입니다"));
    }

    @Test
    @DisplayName("특정 모집글에 속해있는 모든 공지글을 가져온다")
    void findAllNotice() throws Exception {
        //given
        given(noticeService.findAll(1L)).willReturn(new NoticesResponse(
                List.of(new NoticeResponse(1L, "content1", "2023-04-01", true,
                                Map.of("ATTENDANCE", List.of(new Member(1L, "member1"), new Member(2L, "member2")))),
                        new NoticeResponse(2L, "content2", "2023-04-03", true,
                                Map.of("ATTENDANCE", List.of(new Member(1L, "member1"), new Member(2L, "member2"))))
        )));

        //when
        ResultActions action = mvc.perform(get("/recruitment/{recruitmentId}/notice", 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"));

        //then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.notices.size()").value(2));
    }
}