package com.moa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.base.WithMockCustomUser;
import com.moa.domain.possible.PossibleTime;
import com.moa.dto.possible.PossibleTimeRequest;
import com.moa.dto.possible.PossibleTimeResponse;
import com.moa.global.config.WebBeanConfig;
import com.moa.service.PossibleTimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.moa.support.fixture.PossibleTimeFixture.*;
import static com.moa.support.fixture.UserFixture.JHS;
import static com.moa.support.fixture.UserFixture.PINGU;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WithMockCustomUser
@Import(WebBeanConfig.class)
@WebMvcTest(PossibleTimeController.class)
class PossibleTimeControllerTest {
    @MockBean
    private PossibleTimeService possibleTimeService;
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

    @DisplayName("전체 멤버의 참여 가능 시간을 조회한다.")
    @Test
    void getAllMembersTimeList() throws Exception {
        //given
        final Long recruitmentId = 3L;
        List<PossibleTimeResponse> responses = List.of(
                PossibleTimeResponse.builder()
                        .nickname(PINGU.getNickname())
                        .possibleTimes(List.of(
                                TIME1.빈_객체_생성(),
                                TIME3.빈_객체_생성(),
                                TIME8.빈_객체_생성()
                        )).build(),
                PossibleTimeResponse.builder()
                        .nickname(JHS.getNickname())
                        .possibleTimes(List.of(
                                TIME2.빈_객체_생성(),
                                TIME3.빈_객체_생성(),
                                TIME6.빈_객체_생성()
                        )).build()
        );

        given(possibleTimeService.getAllMembersTimeList(recruitmentId))
                .willReturn(responses);

        //when
        ResultActions actions = mvc.perform(
                get("/recruitment/{recruitmentId}/time/all", recruitmentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        actions.andExpectAll(
                status().isOk(),
                jsonPath("$.[0].nickname").value(PINGU.getNickname()),
                jsonPath("$.[1].possibleTimeData.[0]")
                        .value(TIME2.getStartTime() + ":00.000Z")
        );

        verify(possibleTimeService).getAllMembersTimeList(recruitmentId);
    }

    @DisplayName("개인의 참여 가능 시간을 조회한다.")
    @Test
    void getTimeList() throws Exception {
        //given
        final Long recruitmentId = 3L;
        List<PossibleTime> possibleTimes = List.of(
                TIME2.빈_객체_생성(),
                TIME3.빈_객체_생성(),
                TIME6.빈_객체_생성()
        );
        List<String> possibleTimeData = PossibleTimeResponse.getPossibleTimeData(possibleTimes);

        given(possibleTimeService.getTimeList(recruitmentId, 1L))
                .willReturn(possibleTimeData);

        //when
        ResultActions actions = mvc.perform(
                get("/recruitment/{recruitmentId}/time", recruitmentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        actions.andExpectAll(
                status().isOk(),
                jsonPath("$.[0]")
                        .value(TIME2.getStartTime() + ":00.000Z")
        );

        verify(possibleTimeService).getTimeList(recruitmentId, 1L);
    }

    @DisplayName("참여 가능 시간 수정에 성공한다.")
    @Test
    void setPossibleTime() throws Exception {
        //given
        final Long recruitmentId = 3L;
        List<String> possibleTimeData = PossibleTimeResponse.getPossibleTimeData(
                List.of(
                        TIME2.빈_객체_생성(),
                        TIME3.빈_객체_생성(),
                        TIME6.빈_객체_생성()
                )
        );
        List<LocalDateTime> input = new ArrayList<>();
        for (String possibleTimeDatum : possibleTimeData) {
            input.add(LocalDateTime.parse(possibleTimeDatum, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        }
        PossibleTimeRequest possibleTimeRequest = new PossibleTimeRequest(input);

        //when
        ResultActions actions = mvc.perform(
                put("/recruitment/{recruitmentId}/time", recruitmentId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(possibleTimeRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        actions.andExpectAll(
                status().isFound()
        );

        verify(possibleTimeService).setTime(any(PossibleTimeRequest.class), anyLong(), anyLong());
    }

    @DisplayName("참여 가능 시간 수정에 실패한다. (LocalTime 형식에 맞지 않음)")
    @Test
    void setPossibleTime_InvalidLocalTime() throws Exception {
        //given
        String jsonData = "{\n" +
                "   \"possibleTimeDataList\":[\n" +
                "      {\n" +
                "         \"day\":\"MONDAY\",\n" +
                "         \"startTime\":\"17:07:49\",\n" +
                "         \"endTime\":\"19:00\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"day\":\"TUESDAY\",\n" +
                "         \"startTime\":\"09:00\",\n" +
                "         \"endTime\":\"23:00\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"day\":\"SATURDAY\",\n" +
                "         \"startTime\":\"13:00\",\n" +
                "         \"endTime\":\"19:00\"\n" +
                "      }\n" +
                "   ]\n" +
                "}";

        //when
        ResultActions actions = mvc.perform(
                put("/recruitment/{recruitmentId}/time", 3L)
                        .contentType(APPLICATION_JSON)
                        .content(jsonData)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer Token")
        );

        //then
        actions.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.code").value("400"),
                jsonPath("$.message").isString()
        );

        verify(possibleTimeService, times(0)).setTime(any(PossibleTimeRequest.class), anyLong(), anyLong());
    }
}