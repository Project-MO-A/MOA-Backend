package com.moa.controller;

import com.moa.base.AbstractControllerTest;
import com.moa.dto.user.UserInfoResponse;
import com.moa.dto.user.UserSignupRequest;
import com.moa.dto.user.UserUpdateRequest;
import com.moa.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService userService;

    @BeforeEach
    void init() {
        String email = "user@email.com";
        UserSignupRequest request = UserSignupRequest.builder()
                .email(email)
                .password("password")
                .name("name")
                .build();

        userService.saveUser(request);
    }

    @Test
    @DisplayName("signUp")
    void signUp() throws Exception {
        //given
        List<String> interests = new ArrayList<>();
        interests.add("프론트엔드");
        interests.add("자바스크립트");

        UserSignupRequest request = UserSignupRequest.builder()
                .email("user1@email.com")
                .password("password")
                .name("name")
                .nickname("nickname")
                .details("details")
                .locationLatitude(34.123)
                .locationLongitude(35.264)
                .interests(interests)
                .build();

        //when
        ResultActions action = mvc.perform(post("/user/sign-up")
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        action.andExpectAll(
                        status().isCreated(),
                        jsonPath("$.email").exists(),
                        jsonPath("$.email").value("user1@email.com"))
                .andDo(
                        document("user/signup",
                                requestFields(
                                        fieldWithPath("email").type(STRING).description("email"),
                                        fieldWithPath("password").type(STRING).description("password"),
                                        fieldWithPath("name").type(STRING).description("name"),
                                        fieldWithPath("nickname").type(STRING).description("nickname").optional(),
                                        fieldWithPath("details").type(STRING).description("details").optional(),
                                        fieldWithPath("locationLatitude").type(NUMBER).description("locationLatitude").optional(),
                                        fieldWithPath("locationLongitude").type(NUMBER).description("locationLongitude").optional(),
                                        fieldWithPath("interests").type(ARRAY).description("interests of list").optional()
                                ),
                                responseFields(
                                        fieldWithPath("email").type(STRING).description("return email info")
                                )
                        )
                );
    }

    @Test
    @DisplayName("signUp - 중복 사용자 회원가입 실패")
    void signUpFail() throws Exception {
        //given
        UserSignupRequest request = UserSignupRequest.builder()
                .email("user@email.com")
                .password("password")
                .name("name")
                .build();

        //when
        ResultActions action = mvc.perform(post("/user/sign-up")
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        action.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.statusCode").value(400),
                jsonPath("$.errorMessage").value("해당 이메일로 가입이 불가능합니다")
        );
    }

    @Test
    @DisplayName("signOut - 인증 된 사용자 접근 성공")
    @WithMockUser(roles = "USER")
    void signOutSuccess() throws Exception {
        //given
        String email = "user@email.com";

        //when
        ResultActions action = mvc.perform(delete("/user/sign-out?email="+email)
                .header(HttpHeaders.AUTHORIZATION, "access.jwt.token")
                .header("AuthorizationRefresh", "refresh.jwt.token"));

        //then
        action.andExpect(status().isOk())
                .andDo(
                        document("user/signout",
                                queryParameters(
                                        parameterWithName("email").description("user email")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization").description("access token"),
                                        headerWithName("AuthorizationRefresh").description("refresh token").optional()
                                )
                        )
                );
    }

    @Test
    @DisplayName("signOut - 인증 되지 않은 사용자 접근 실패")
    @WithAnonymousUser
    void signOutFail() throws Exception {
        //given
        String email = "user@email.com";

        //when
        ResultActions action = mvc.perform(delete("/user/sign-out")
                .param("email", email));

        //then
        action.andExpect(status().isForbidden());
    }

    @DisplayName("사용자 개인정보 관련 기능")
    @Nested
    class Information {
        final String EMAIL = "test@email.com";
        @BeforeEach
        void setUp() {
            //given
            List<String> interests = new ArrayList<>();
            interests.add("Java");
            interests.add("Python");

            UserSignupRequest request = UserSignupRequest.builder()
                    .email(EMAIL)
                    .password("password")
                    .name("name")
                    .nickname("John")
                    .locationLongitude(34.14123)
                    .locationLatitude(34.14123)
                    .details("Hello")
                    .interests(interests)
                    .build();

            userService.saveUser(request);
        }

        @Test
        @DisplayName("getInfo - 사용자의 정보를 가져오는데 성공한다.")
        @WithMockUser
        void getInfo() throws Exception {
            //when
            ResultActions action = mvc.perform(get("/user/info?email="+EMAIL)
                    .header(HttpHeaders.AUTHORIZATION, "access.jwt.token")
                    .header("AuthorizationRefresh", "refresh.jwt.token"));

            action.andExpectAll(
                    status().isOk(),
                    jsonPath("$.email").value(EMAIL),
                    jsonPath("$.name").value("name"),
                    jsonPath("$.nickname").value("John"),
                    jsonPath("$.locationLatitude").value(34.14123),
                    jsonPath("$.locationLongitude").value(34.14123),
                    jsonPath("$.details").value("Hello"),
                    jsonPath("$.interests.[0]").value("Java"),
                    jsonPath("$.interests.[1]").value("Python")
            );
        }

        @Test
        @DisplayName("getInfo - 사용자의 정보를 가져오는데 실패한다. (잘못된 이메일)")
        @WithMockUser
        void getInfoFail1() throws Exception {
            //when
            String none = "none@email.com";
            ResultActions action = mvc.perform(get("/user/info?email="+none)
                    .header(HttpHeaders.AUTHORIZATION, "access.jwt.token")
                    .header("AuthorizationRefresh", "refresh.jwt.token"));

            //400
            action.andExpect(status().isBadRequest());
        }

        @DisplayName("update - 사용자 정보를 수정하는데 성공한다.")
        @WithMockUser
        @Test
        void update() throws Exception {
            //given
            List<String> interest = new ArrayList<>();
            interest.add("AWS");
            interest.add("Jenkins");

            UserUpdateRequest request = UserUpdateRequest.builder()
                    .email(EMAIL)
                    .name("Jenny")
                    .nickname("Honey")
                    .locationLatitude(25.1234123)
                    .locationLongitude(25.1234123)
                    .details("Bye")
                    .interests(interest)
                    .build();

            //when
            ResultActions action = mvc.perform(put("/user/info")
                    .content(toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "access.jwt.token")
                    .header("AuthorizationRefresh", "refresh.jwt.token"));

            action.andExpect(status().isNoContent());
            UserInfoResponse info = userService.getUserInfoByEmail(EMAIL);
            assertThat(info.getName()).isEqualTo("Jenny");
            assertThat(info.getNickname()).isEqualTo("Honey");
            assertThat(info.getDetails()).isEqualTo("Bye");
            assertThat(info.getInterests()).containsOnly("AWS", "Jenkins");
        }

        @DisplayName("update - 사용자 정보를 수정하는데 실패한다. (validation)")
        @WithMockUser
        @Test
        void updateFail1() throws Exception {
            //given
            List<String> interest = new ArrayList<>();
            interest.add("AWS");
            interest.add("Jenkins");

            UserUpdateRequest request = UserUpdateRequest.builder()
                    .email(EMAIL)
                    .name("Very very very very very very very very long name")
                    .nickname("Honey")
                    .locationLatitude(25.1234123)
                    .locationLongitude(25.1234123)
                    .details("Bye")
                    .interests(interest)
                    .build();

            //when
            ResultActions action = mvc.perform(put("/user/info")
                    .content(toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "access.jwt.token")
                    .header("AuthorizationRefresh", "refresh.jwt.token"));

            action.andExpectAll(status().isBadRequest());
        }

        @DisplayName("update - 사용자 정보를 수정하는데 실패한다. (잘못된 이메일)")
        @WithMockUser
        @Test
        void updateFail2() throws Exception {
            //given
            List<String> interest = new ArrayList<>();
            interest.add("AWS");
            interest.add("Jenkins");

            UserUpdateRequest request = UserUpdateRequest.builder()
                    .email("none@email.com")
                    .name("Very very very very very very very very long name")
                    .nickname("Honey")
                    .locationLatitude(25.1234123)
                    .locationLongitude(25.1234123)
                    .details("Bye")
                    .interests(interest)
                    .build();

            //when
            ResultActions action = mvc.perform(put("/user/info")
                    .content(toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "access.jwt.token")
                    .header("AuthorizationRefresh", "refresh.jwt.token"));

            action.andExpectAll(status().isBadRequest());
        }
    }

}