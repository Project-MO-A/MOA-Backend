package com.moa.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.dto.user.UserSignupRequest;
import com.moa.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureRestDocs
class LoginProcessFilterTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RestDocumentationContextProvider restDocumentation;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    protected MockMvc mvc;

    @Autowired
    private UserService userService;

    private UserSignupRequest request;

    @BeforeEach
    void setup() throws Exception {
        LoginProcessFilter loginProcessFilter = new LoginProcessFilter(mapper, authenticationConfiguration.getAuthenticationManager());

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(loginProcessFilter)
                .build();

        request = UserSignupRequest.builder()
                .email("user@email.com")
                .password("password")
                .name("name")
                .nickname("nickname")
                .details("details")
                .locationLatitude(34.123)
                .locationLongitude(35.264)
                .interests(List.of("프론트엔드", "자바스크립트"))
                .build();
    }

    @Test
    @DisplayName("loginSuccess")
    void loginSuccess() throws Exception {
        //given
        userService.saveUser(request);

        //when
        ResultActions action = mvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new Login("user@email.com", "password"))));

        //then
        action.andExpect(status().isOk())
                .andDo(
                        document("user/login",
                                requestFields(
                                        fieldWithPath("email").description("user email"),
                                        fieldWithPath("password").description("password")
                                ),
                                responseHeaders(
                                        headerWithName("Authorization").description("Issued accessToken"),
                                        headerWithName("AuthorizationRefresh").description("Issued refreshToken")
                                )
                        )
                );
    }

    @Test
    @DisplayName("loginFail")
    void loginFail() throws Exception {
        //given
        userService.saveUser(request);

        //when
        ResultActions action = mvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new Login("incorrect@email.com", "password"))));

        //then
        action.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value("해당 이메일을 가진 유저를 찾을 수 없습니다")
        );
    }

    @Test
    @DisplayName("loginFail")
    void loginFailInvalidPassword() throws Exception {
        //given
        userService.saveUser(request);

        //when
        ResultActions action = mvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(new Login("user@email.com", "password11"))));

        //then
        action.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").value("비밀번호를 다시 입력해 주세요")
        );
    }

    private record Login(String email, String password) {
    }

}