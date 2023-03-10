package com.moa.service;

import com.moa.base.AbstractServiceTest;
import com.moa.domain.user.User;
import com.moa.dto.user.UserEmailResponse;
import com.moa.dto.user.UserSignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.moa.constant.TestConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UserServiceUnitTest extends AbstractServiceTest {

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("userDetails 형식의 유저 찾기")
    void findUserWithSecurity() {
        //given
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(USER));

        //when
        UserDetails findUser = userService.loadUserByUsername(EMAIL);

        //then
        assertThat(USER.getName()).isEqualTo(findUser.getUsername());
    }

    @Test
    @DisplayName("일치하지 않은 정보로 유저 찾기")
    void findUserWithIncorrectInfo() {
        //given
        given(userRepository.findByEmail(INCORRECT_EMAIL)).willThrow(UsernameNotFoundException.class);

        //when & then
        assertThatThrownBy(() -> userService.loadUserByUsername(INCORRECT_EMAIL))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("유저 저장")
    void saveUser() {
        //given
        given(userRepository.save(any(User.class))).willReturn(USER);
        UserSignupRequest request = UserSignupRequest.builder()
                .email("user@email.com")
                .password("password")
                .name("name")
                .nickname("nickname")
                .locationLatitude(23.23)
                .locationLongitude(34.3443)
                .details("details")
                .build();

        //when
        UserEmailResponse userEmailResponse = userService.saveUser(request);

        //then
        assertThat(userEmailResponse.email()).isEqualTo(USER.getEmail());
    }

    @Test
    @DisplayName("유저 삭제")
    void deleteUser() {
        //given
        given(userRepository.findByEmail(USER.getEmail())).willReturn(Optional.of(USER));

        //when
        userService.deleteUser(USER.getEmail());

        //then
        verify(userRepository, times(1)).delete(USER);
    }

    @Test
    @DisplayName("잘못된 이메일로 유저 삭제 불가")
    void cannotDeleteUser() {
        //given
        given(userRepository.findByEmail(INCORRECT_EMAIL)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> userService.deleteUser(INCORRECT_EMAIL))
                .isInstanceOf(UsernameNotFoundException.class);

    }

}