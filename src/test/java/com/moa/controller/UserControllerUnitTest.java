package com.moa.controller;

import com.moa.base.AbstractControllerUnitTest;
import com.moa.dto.user.*;
import com.moa.global.exception.BusinessException;
import com.moa.global.exception.GlobalControllerAdvice;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.moa.global.exception.ErrorCode.USER_DUPLICATED_EMAIL;
import static com.moa.global.exception.ErrorCode.USER_NOT_FOUND;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, GlobalControllerAdvice.class})
public class UserControllerUnitTest extends AbstractControllerUnitTest {

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("회원가입에 성공한다")
    void successSignUp() throws Exception {
        //given
        UserSignupRequest request = new UserSignupRequest("user@email.com", "password", "name", "nickname", "details",
        34.232, 45.324, List.of("백엔드", "자바"));
        given(userService.saveUser(request)).willReturn(new UserEmailResponse("user@email.com"));

        //when
        ResultActions action = mvc.perform(post("/user/sign-up")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원 이메일 중복으로 회원가입에 실패한다")
    void failSignUpByDuplicatedEmail() throws Exception {
        //given
        UserSignupRequest request = new UserSignupRequest("user@email.com", "password", "name", "nickname", "details",
                34.232, 45.324, List.of("백엔드", "자바"));
        given(userService.saveUser(request))
                .willThrow(new BusinessException(USER_DUPLICATED_EMAIL));

        //when
        ResultActions action = mvc.perform(post("/user/sign-up")
                .header(AUTHORIZATION, "Bearer Token")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 정보 수정에 성공한다")
    void successUpdateUserInfo() throws Exception {
        //given
        UserUpdateRequest request = new UserUpdateRequest("user@email.com", "name",
                "nickname", 1.0, 1.0, null, null);
        willDoNothing().given(userService).updateUser(request);

        //when
        ResultActions action = mvc.perform(put("/user/info")
                .header(AUTHORIZATION, "Bearer Token")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("회원찾기에 실패하여 정보 수정에 실패한다")
    void failUpdateUserInfo() throws Exception {
        //given
        UserUpdateRequest request = new UserUpdateRequest("invalid@email.com", "name",
                "nickname", 1.0, 1.0, null, null);
        willThrow(new EntityNotFoundException(USER_NOT_FOUND)).given(userService).updateUser(request);

        //when
        ResultActions action = mvc.perform(put("/user/info")
                .header(AUTHORIZATION, "Bearer Token")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("비밀번호 변경에 성공한다")
    void successChangePassword() throws Exception {
        //given
        UserPwUpdateRequest request = new UserPwUpdateRequest("user@emai.com", "current", "new");
        willDoNothing().given(userService).changePassword(request);

        //when
        ResultActions action = mvc.perform(put("/user/pw")
                .header(AUTHORIZATION, "Bearer Token")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("회원찾기에 실패하여 비밀번호 수정에 실패한다")
    void failChangePassword() throws Exception {
        //given
        UserPwUpdateRequest request = new UserPwUpdateRequest("invalid@emai.com", "current", "new");
        willThrow(new EntityNotFoundException(USER_NOT_FOUND)).given(userService).changePassword(request);

        //when
        ResultActions action = mvc.perform(put("/user/pw")
                .header(AUTHORIZATION, "Bearer Token")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원 중복 여부를 확인한다 - 중복")
    void checkDuplicated() throws Exception {
        //given
        EmailDuplicateRequest request = new EmailDuplicateRequest("user@email.com");
        given(userService.checkEmailUnique(request.email())).willReturn(true);

        //when
        ResultActions action = mvc.perform(get("/user/check/email")
                .header(AUTHORIZATION, "Bearer Token")
                .contentType(APPLICATION_JSON)
                .content(toJson(request)));

        //then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(true));
    }

}
