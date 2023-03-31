package com.moa.service.unit;

import com.moa.base.AbstractServiceTest;
import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApprovalStatus;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import com.moa.dto.recruit.RecruitmentsInfo;
import com.moa.dto.user.*;
import com.moa.global.exception.BusinessException;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static com.moa.constant.TestConst.*;
import static com.moa.domain.recruit.Category.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
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
                .isExactlyInstanceOf(UsernameNotFoundException.class);
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
    @DisplayName("중복 유저 저장으로 인한 실패")
    void failSaveUserByDuplicated() {
        //given
        given(userRepository.findByEmail("duplicate@email.com")).willReturn(Optional.of(USER));
        UserSignupRequest request = new UserSignupRequest("duplicate@email.com", "password", "name",
                null, null, 0.0, 0.0, null);
        //when & then
        assertThatThrownBy(() -> userService.saveUser(request))
                .isExactlyInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("유저 기본 정보 조회")
    void getUserProfile() {
        //given
        given(userRepository.findById(1L)).willReturn(Optional.of(USER));

        //when
        UserInfo userInfo = userService.getUserProfileInfoById(1L);

        //then
        assertThat(userInfo.name()).isEqualTo(USER.getName());
        assertThat(userInfo.email()).isEqualTo(USER.getEmail());
        assertThat(userInfo.details()).isEqualTo(USER.getDetails());
    }

    @Test
    @DisplayName("가입되지 않은 유저 기본 정보 조회 실패")
    void failGetUserProfile() {
        //given
        given(userRepository.findById(0L)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> userService.getUserProfileInfoById(0L))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }
    @Test
    @DisplayName("유저 작성 글 조회")
    void getUserWriting() {
        //given
        List<Recruitment> value = List.of(
                new Recruitment(USER, new Post("title1", "content1"), RecruitStatus.RECRUITING, PROGRAMMING),
                new Recruitment(USER, new Post("title2", "content2"), RecruitStatus.RECRUITING, PROGRAMMING),
                new Recruitment(USER, new Post("title3", "content3"), RecruitStatus.COMPLETE, PROGRAMMING)
        );
        given(recruitmentRepository.findListByIdFetchUser(1L)).willReturn(value);

        //when
        RecruitmentsInfo info = userService.getUserWritingInfoById(1L);

        //then
        assertThat(info.getWriting().size()).isEqualTo(3);
        assertThat(info.getWriting().get(0).title()).isEqualTo("title1");
        assertThat(info.getWriting().get(0).recruitStatus()).isEqualTo("모집중");
    }

    @Test
    @DisplayName("유저 활동 내용 조회")
    void getUserActivity() {
        //given
        Recruitment recruitment1 = new Recruitment(
                User.builder().email("admin@email.com").build(),
                new Post("recruitTitle", "content"),
                RecruitStatus.COMPLETE, PROGRAMMING);
        Recruitment recruitment2 = new Recruitment(
                User.builder().email("admin@email.com").build(),
                new Post("recruitTitle1", "content1"),
                RecruitStatus.RECRUITING, PROGRAMMING);

        List<ApplimentMember> value = List.of(
                new ApplimentMember(
                        new RecruitMember(recruitment1,"backend", 2),
                        USER,
                        ApprovalStatus.APPROVED),
                new ApplimentMember(
                        new RecruitMember(recruitment2,"backend", 2),
                        USER,
                        ApprovalStatus.PENDING)
                );
        given(applimentMemberRepository.findAllRecruitmentByUserId(1L)).willReturn(value);

        //when
        UserActivityInfo info = userService.getUserActivityInfoById(1L);

        //then
        assertThat(info.getApprovedProjects().size()).isEqualTo(2);
        assertThat(info.getApprovedProjects().get("COMPLETE").size()).isEqualTo(1);
        assertThat(info.getApprovedProjects().get("COMPLETE").get(0).title()).isEqualTo("recruitTitle");
        assertThat(info.getApprovedProjects().get("COMPLETE").get(0).detailsUri()).startsWith("/recruitment/");
        assertThat(info.getEtcProjects().size()).isEqualTo(1);
        assertThat(info.getEtcProjects().get(0).title()).isEqualTo("recruitTitle1");
        assertThat(info.getEtcProjects().get(0).cancelUri()).startsWith("/recruitment/cancel/");
        assertThat(info.getEtcProjects().get(0).detailsUri()).startsWith("/recruitment/");
        assertThat(info.getEtcProjects().get(0).status()).isEqualTo("대기중");
    }

    @Test
    @DisplayName("유저 관심 글 조회")
    void getUserConcern() {
        //given
        given(userRepository.findRecruitmentInterestById(1L)).willReturn(Optional.of(ASSOCIATION_USER));

        //when
        UserRecruitmentInterestInfo info = userService.getUserConcernInfoById(1L);

        //then
        assertThat(info.getWriting().size()).isEqualTo(1);
        assertThat(info.getWriting().get(0).title()).isEqualTo("title");
        assertThat(info.getWriting().get(0).redirectUri()).startsWith("/recruitment/");
    }

    @Test
    @DisplayName("가입되지 않은 유저의 관심글 조회 실패")
    void failGetUserConcern() {
        //given
        given(userRepository.findRecruitmentInterestById(0L)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> userService.getUserConcernInfoById(0L))
                .isExactlyInstanceOf(EntityNotFoundException.class);

    }
    @Test
    @DisplayName("유저 삭제")
    void deleteUser() {
        //given
        given(userRepository.findById(1L)).willReturn(Optional.of(USER));

        //when
        userService.deleteUser(1L);

        //then
        verify(userRepository, times(1)).delete(USER);
    }

    @Test
    @DisplayName("잘못된 이메일로 유저 삭제 불가")
    void cannotDeleteUser() {
        //given
        given(userRepository.findById(0L)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> userService.deleteUser(0L))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("유저 정보 변경 성공")
    void successChangeUserProfile() {
        //given
        UserUpdateRequest request = new UserUpdateRequest(USER.getEmail(), "newName", USER.getNickname(), USER.getLocationLatitude(), USER.getLocationLongitude(), null, null);
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(USER));

        //when
        userService.updateUser(request);

        //then
        assertAll(
                () -> assertThat(USER.getName()).isEqualTo("newName"),
                () -> assertThat(USER.getDetails()).isEqualTo(USER.getDetails())
        );
    }

    @Test
    @DisplayName("존재하지 않은 유저 정보 실패")
    void failChangeUserProfile() {
        //given
        UserUpdateRequest request = new UserUpdateRequest(USER.getEmail(), "newName", USER.getNickname(), USER.getLocationLatitude(), USER.getLocationLongitude(), null, null);
        given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> userService.updateUser(request))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }
    @Test
    @DisplayName("유저 비밀번호 변경 성공")
    void successChangePassword() {
        //given
        UserPwUpdateRequest request = new UserPwUpdateRequest(USER.getEmail(), USER.getPassword(), "newPassword");
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(USER));
        given(passwordEncoder.matches(request.currentPassword(), USER.getPassword())).willReturn(true);
        given(passwordEncoder.encode(request.newPassword())).willReturn(request.newPassword());

        //when
        userService.changePassword(request);

        //then
        assertThat(USER.getPassword()).isEqualTo("newPassword");
    }

    @Test
    @DisplayName("가입되 있지 않은 유저의 비밀번호 변경 실패")
    void failChangePasswordByNoUser() {
        //given
        UserPwUpdateRequest request = new UserPwUpdateRequest(USER.getEmail(), USER.getPassword(), "newPassword");
        given(userRepository.findByEmail(request.email())).willThrow(EntityNotFoundException.class);

        //when & then
        assertAll(
                () -> assertThatThrownBy(() -> userService.changePassword(request))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(passwordEncoder, never()).matches(request.currentPassword(), request.newPassword())
        );
    }

    @Test
    @DisplayName("기존 비밀번호를 잘못 입력해서 비밀번호 변경 실패")
    void failChangePasswordByIncorrectBeforePassword() {
        //given
        UserPwUpdateRequest request = new UserPwUpdateRequest(USER.getEmail(), "wrongPassword", "newPassword");
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(USER));
        given(passwordEncoder.matches(request.currentPassword(), USER.getPassword())).willReturn(false);

        //when & then
        assertAll(
                () -> assertThatThrownBy(() -> userService.changePassword(request))
                        .isExactlyInstanceOf(BusinessException.class),
                () -> verify(userRepository).findByEmail(request.email()),
                () -> verify(passwordEncoder).matches(request.currentPassword(), USER.getPassword())
        );
    }

}