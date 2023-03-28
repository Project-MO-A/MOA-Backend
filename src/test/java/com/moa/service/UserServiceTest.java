package com.moa.service;

import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.user.*;
import com.moa.global.exception.BusinessException;
import com.moa.global.exception.service.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class UserServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    private static final String TEST_EMAIL = "test2@naver.com";

    @BeforeEach
    void setUp() {
        List<String> interests = new ArrayList<>();
        interests.add("Java");
        interests.add("Python");

        UserSignupRequest testUser = UserSignupRequest.builder()
                .email(TEST_EMAIL)
                .password("qwer1234")
                .name("기우")
                .nickname("john")
                .details("Hello")
                .locationLatitude(23.1551134)
                .locationLongitude(51.2341355)
                .interests(interests)
                .build();

        userService.saveUser(testUser);
    }

    @DisplayName("info - 유저의 개인 정보를 가져온다 fail -> 모집 글 기능 구현시 테스트 재 구성 필")
    @Test
    void info() {
        //when
        User user = userRepository.findByEmail(TEST_EMAIL).get();
        UserInfoResponse info = userService.getUserInfoById(user.getId());

        //then
        assertThat(info.getUserInfo().email()).isEqualTo("test2@naver.com");
        assertThat(info.getUserInfo().popularity()).isEqualTo(0);
        assertThat(info.getUserInfo().nickname()).isEqualTo("john");
        assertThat(info.getUserInfo().details()).isEqualTo("Hello");
        assertThat(info.getUserInfo().interests()).containsOnly("Java", "Python");
    }

    @DisplayName("update - 유저의 개인 정보를 수정한다.")
    @Test
    void update() {
        //given
        List<String> interests = new ArrayList<>();
        interests.add("C#");
        interests.add("JavaScript");

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .email(TEST_EMAIL)
                .name("기우")
                .nickname("bam")
                .details("Hello bro")
                .locationLatitude(23.1551134)
                .locationLongitude(51.2341355)
                .interests(interests)
                .build();

        //when
        userService.updateUser(updateRequest);

        //then
        User user = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertThat(user.getDetails()).isEqualTo("Hello bro");
        assertThat(user.getNickname()).isEqualTo("bam");
        assertThat(user.getInterests().get(0).getName()).isEqualTo("C#");
    }

    @DisplayName("update - 유저의 개인 정보를 수정하는데 실패한다. (잘못된 이메일)")
    @Test
    void updateFail1() {
        //given
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .email("ssss@naver.com")
                .name("기우")
                .nickname("bam")
                .details("Hello bro")
                .locationLatitude(23.1551134)
                .locationLongitude(51.2341355)
                .build();

        //when
        assertThatThrownBy(() -> userService.updateUser(updateRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("changePassword - 유저 비밀번호 변경에 성공한다.")
    @Test
    void updatePassword() {
        //given
        String newPw = "different1234";
        UserPwUpdateRequest pwUpdateRequest = UserPwUpdateRequest.builder()
                .email(TEST_EMAIL)
                .currentPassword("qwer1234")
                .newPassword(newPw)
                .build();

        //when
        userService.changePassword(pwUpdateRequest);

        //then
        User user = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertThat(passwordEncoder.matches(newPw, user.getPassword())).isTrue();
    }

    @DisplayName("changePassword - 유저 비밀번호 변경에 실패한다. (잘못된 이메일)")
    @Test
    void updatePasswordFail1() {
        //given
        UserPwUpdateRequest pwUpdateRequest = UserPwUpdateRequest.builder()
                .email("wrongEmail@email.com")
                .currentPassword("qwer1234")
                .newPassword("different1234")
                .build();

        //when & then
        assertThatThrownBy(() -> userService.changePassword(pwUpdateRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("changePassword - 유저 비밀번호 변경에 실패한다. (잘못된 패스워드)")
    @Test
    void updatePasswordFail2() {
        //given
        UserPwUpdateRequest pwUpdateRequest = UserPwUpdateRequest.builder()
                .email(TEST_EMAIL)
                .currentPassword("wrongPW123")
                .newPassword("different1234")
                .build();

        //when
        assertThatThrownBy(() -> userService.changePassword(pwUpdateRequest))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("checkDuplicateEmail - 이메일이 중복되었다면 false 를 반환한다.")
    @Test
    void checkEmailUnique() {
        //when
        Boolean isDuplicate = userService.checkEmailUnique(TEST_EMAIL);

        //then
        assertThat(isDuplicate).isFalse();
    }

    @DisplayName("checkDuplicateEmail - 이메일이 중복되지 않았는다면 true 를 반환한다.")
    @Test
    void checkDuplicateEmailFalse() {
        //when
        Boolean isDuplicate = userService.checkEmailUnique("unique@email.com");

        //then
        assertThat(isDuplicate).isTrue();
    }
}