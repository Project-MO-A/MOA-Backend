package com.moa.service;

import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.user.UserInfoResponse;
import com.moa.dto.user.UserSignupRequest;
import com.moa.dto.user.UserUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @DisplayName("info - 유저의 개인 정보를 가져온다")
    @Test
    void info() {
        //when
        UserInfoResponse info = userService.getUserInfoByEmail(TEST_EMAIL);

        //then
        assertThat(info.getEmail()).isEqualTo("test2@naver.com");
        assertThat(info.getPopularity()).isEqualTo(0);
        assertThat(info.getNickname()).isEqualTo("john");
        assertThat(info.getDetails()).isEqualTo("Hello");
        assertThat(info.getInterests()).containsOnly("Java", "Python");
    }

    @DisplayName("info - 유저의 개인 정보를 가져오는데 실패한다. (잘못된 이메일)")
    @Test
    void infoFail1() {
        //when
        assertThatThrownBy(() -> userService.getUserInfoByEmail("ssss@naver.com"))
                .isInstanceOf(UsernameNotFoundException.class);
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
                .password("test1234")
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
        assertThat(user.getPassword()).isEqualTo("test1234");
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
                .password("test1234")
                .name("기우")
                .nickname("bam")
                .details("Hello bro")
                .locationLatitude(23.1551134)
                .locationLongitude(51.2341355)
                .build();

        //when
        assertThatThrownBy(() -> userService.updateUser(updateRequest))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}