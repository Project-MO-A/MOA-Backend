package com.moa.service;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.StatusResponse;
import com.moa.dto.member.RecruitMemberRequest;
import com.moa.dto.recruit.RecruitInfoResponse;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.recruit.RecruitUpdateRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.global.exception.service.InvalidCodeException;
import com.moa.global.exception.service.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.moa.domain.recruit.RecruitStatus.getInstance;
import static com.moa.support.fixture.RecruitMemberFixture.BACKEND_MEMBER;
import static com.moa.support.fixture.RecruitMemberFixture.FRONTEND_MEMBER;
import static com.moa.support.fixture.RecruitRequestFixture.ANOTHER_REQUEST;
import static com.moa.support.fixture.RecruitRequestFixture.BASIC_REQUEST;
import static com.moa.support.fixture.RecruitmentFixture.PROGRAMMING_POST;
import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.TagFixture.FRONTEND_TAG;
import static com.moa.support.fixture.UserFixture.PINGU;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {
    @Mock
    private RecruitmentRepository recruitmentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecruitmentService recruitmentService;

    @DisplayName("post - 모집글 등록에 성공한다.")
    @Test
    void post() throws NoSuchFieldException, IllegalAccessException {
        //given
        final Long userId = 1L;
        final List<Tag> tags = BACKEND_TAG.생성();
        RecruitPostRequest postRequest = BASIC_REQUEST.등록_생성();

        // Reflection
        Recruitment recruitment = PROGRAMMING_POST.생성();
        Field id = recruitment.getClass().getDeclaredField("id");
        id.setAccessible(true);
        id.set(recruitment, 1L);

        given(userRepository.getReferenceById(userId)).willReturn(User.builder().build());
        given(recruitmentRepository.save(any())).willReturn(recruitment);


        //when
        Long postId = recruitmentService.post(userId, postRequest, tags);

        //then
        assertAll(
                () -> assertThat(postId).isEqualTo(1L),
                () -> verify(userRepository).getReferenceById(userId),
                () -> verify(recruitmentRepository).save(any(Recruitment.class))
        );
    }

    @DisplayName("post - 모집 멤버가 null일 경우 예외가 발생한다.")
    @Test
    void post_memberNull() {
        //given
        final Long userId = 1L;
        final List<Tag> tags = BACKEND_TAG.생성();
        RecruitPostRequest postRequest = BASIC_REQUEST.멤버를_변경하여_등록_생성(null);
        given(userRepository.getReferenceById(userId)).willReturn(User.builder().build());

        //then
        assertAll(
                () -> assertThatThrownBy(() -> recruitmentService.post(userId, postRequest, tags))
                        .isExactlyInstanceOf(InvalidRequestException.class),
                () -> verify(userRepository).getReferenceById(userId),
                () -> verify(recruitmentRepository, times(0)).save(any(Recruitment.class))
        );
    }

    @DisplayName("post - 모집 멤버가 empty 일 경우 예외가 발생한다.")
    @Test
    void post_memberEmpty() {
        //given
        final Long userId = 1L;
        final List<Tag> tags = BACKEND_TAG.생성();
        RecruitPostRequest postRequest = BASIC_REQUEST.멤버를_변경하여_등록_생성(new ArrayList<>());
        given(userRepository.getReferenceById(userId)).willReturn(User.builder().build());

        //then
        assertAll(
                () -> assertThatThrownBy(() -> recruitmentService.post(userId, postRequest, tags))
                        .isExactlyInstanceOf(InvalidRequestException.class),
                () -> verify(userRepository).getReferenceById(userId),
                () -> verify(recruitmentRepository, times(0)).save(any(Recruitment.class))
        );
    }

    @DisplayName("모집글 수정, 확인, 삭제 테스트")
    @Nested
    class Service {
        @DisplayName("getInfo - 모집글 정보를 불러오는데 성공한다.")
        @Test
        void getInfo() {
            //given
            final Long recruitId = 3L;
            Recruitment recruitment = PROGRAMMING_POST.생성(PINGU.생성(),
                    BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                    List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성()));
            given(recruitmentRepository.findFetchUserTagsById(recruitId))
                    .willReturn(Optional.of(recruitment));

            //when
            RecruitInfoResponse info = recruitmentService.getInfo(recruitId);

            //then
            assertAll(
                    () -> assertThat(info.getTitle()).isEqualTo(PROGRAMMING_POST.getTitle()),
                    () -> assertThat(info.getPostUser().userName()).isEqualTo(PINGU.getName()),
                    () -> verify(recruitmentRepository).findFetchUserTagsById(recruitId)
            );
        }

        @DisplayName("getInfo - 잘못된 모집글 ID를 입력하면 예외가 발생한다.")
        @Test
        void getInfo_invalidRecruitId() {
            //given
            final Long invalidRecruitId = 999L;
            given(recruitmentRepository.findFetchUserTagsById(invalidRecruitId))
                    .willThrow(EntityNotFoundException.class);

            //when & then
            assertThatThrownBy(() -> recruitmentService.getInfo(invalidRecruitId))
                    .isExactlyInstanceOf(EntityNotFoundException.class);
        }

        @DisplayName("update - 모집글 수정에 성공한다. (타이틀, 내용)")
        @Test
        void updatePost() {
            //given
            final Long recruitId = 1L;
            final List<Tag> updateTag = FRONTEND_TAG.생성();
            RecruitUpdateRequest updateRequest = ANOTHER_REQUEST.수정_생성();
            List<RecruitMember> recruitMembers = List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성());
            Recruitment basicRecruit = PROGRAMMING_POST.생성1(PINGU.생성(),
                    BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                    recruitMembers);

            given(recruitmentRepository.findFetchMembersById(recruitId))
                    .willReturn(Optional.of(basicRecruit));

            //when
            recruitmentService.update(recruitId, updateRequest, updateTag);

            //then
            assertAll(
                    () -> assertThat(basicRecruit.getPost().getTitle()).isEqualTo(ANOTHER_REQUEST.getTitle()),
                    () -> verify(recruitmentRepository).findFetchMembersById(recruitId)
            );
        }

        @DisplayName("update - 모집글 수정에 성공한다. (모집 멤버 그룹)")
        @Test
        void updateRecruitMember() {
            //given
            final Long recruitId = 1L;
            final List<Tag> updateTag = FRONTEND_TAG.생성();
            RecruitUpdateRequest updateRequest = ANOTHER_REQUEST.멤버를_변경하여_수정_생성(
                    List.of(
                            RecruitMemberRequest.builder().field("디자이너").total(2).build(),
                            RecruitMemberRequest.builder().field("인프라").total(1).build()
                    )
            );
            List<RecruitMember> beforeMember = List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성());
            Recruitment basicRecruit = PROGRAMMING_POST.생성1(PINGU.생성(),
                    BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                    beforeMember);

            given(recruitmentRepository.findFetchMembersById(recruitId))
                    .willReturn(Optional.of(basicRecruit));

            //when
            recruitmentService.update(recruitId, updateRequest, updateTag);

            //then
            RecruitMember recruitMember1 = basicRecruit.getMembers().get(0);
            RecruitMember recruitMember2 = basicRecruit.getMembers().get(1);
            for (RecruitMember member : basicRecruit.getMembers()) {
                System.out.println(member.getRecruitField());
            }
            assertAll(
                    () -> assertThat(recruitMember1.getRecruitField()).isEqualTo("디자이너"),
                    () -> assertThat(recruitMember2.getRecruitField()).isEqualTo("인프라"),
                    () -> verify(recruitmentRepository).findFetchMembersById(recruitId)
            );
        }

        @DisplayName("update - 모집글 수정에 성공한다. (카테고리)")
        @Test
        void updateCategory() {
            //given
            final Long recruitId = 1L;
            final List<Tag> updateTag = FRONTEND_TAG.생성();
            RecruitUpdateRequest updateRequest = ANOTHER_REQUEST.수정_생성();
            List<RecruitMember> recruitMembers = List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성());

            Recruitment basicRecruit = PROGRAMMING_POST.생성1(PINGU.생성(),
                    BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                    recruitMembers);
            given(recruitmentRepository.findFetchMembersById(recruitId))
                    .willReturn(Optional.of(basicRecruit));

            //when
            recruitmentService.update(recruitId, updateRequest, updateTag);

            //then
            assertAll(
                    () -> assertThat(basicRecruit.getTags().size()).isEqualTo(updateTag.size()),
                    () -> verify(recruitmentRepository).findFetchMembersById(recruitId)
            );
        }

        @DisplayName("update - 모집 멤버가 없을 경우 예외가 발생한다.")
        @Test
        void update_invalidRequest() {
            //given
            final Long recruitId = 1L;
            final List<Tag> updateTag = FRONTEND_TAG.생성();
            RecruitUpdateRequest updateRequest = ANOTHER_REQUEST.멤버를_변경하여_수정_생성(new ArrayList<>());

            Recruitment basicRecruit = PROGRAMMING_POST.생성(PINGU.생성(),
                    BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                    List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성()));
            given(recruitmentRepository.findFetchMembersById(recruitId))
                    .willReturn(Optional.of(basicRecruit));

            //when & then
            assertThatThrownBy(() -> recruitmentService.update(recruitId, updateRequest, updateTag))
                            .isExactlyInstanceOf(InvalidRequestException.class);
        }

        @DisplayName("update - 잘못된 ID 가 입력될 경우 예외가 발생한다.")
        @Test
        void update_invalidRecruitId() {
            //given
            final Long invalidId = 999L;
            final List<Tag> updateTag = FRONTEND_TAG.생성();
            RecruitUpdateRequest updateRequest = ANOTHER_REQUEST.수정_생성();

            given(recruitmentRepository.findFetchMembersById(invalidId))
                    .willThrow(EntityNotFoundException.class);

            //when & then
            assertThatThrownBy(() -> recruitmentService.update(invalidId, updateRequest, updateTag))
                    .isExactlyInstanceOf(EntityNotFoundException.class);
        }

        @DisplayName("updateStatus - 모집글 상태 변경에 성공한다.")
        @Test
        void updateState() {
            //given
            final Long recruitId = 1L;
            final int statusCode = 2;

            Recruitment basicRecruit = PROGRAMMING_POST.생성(PINGU.생성(),
                    BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                    List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성()));
            given(recruitmentRepository.findById(recruitId))
                    .willReturn(Optional.of(basicRecruit));

            //when
            StatusResponse statusResponse = recruitmentService.updateStatus(recruitId, statusCode);

            //then
            assertAll(
                    () -> assertThat(statusResponse.status()).isEqualTo(getInstance(statusCode).name()),
                    () -> verify(recruitmentRepository).findById(recruitId)
            );
        }

        @DisplayName("updateStatus - 잘못된 상태 코드일 경우 예외가 발생한다.")
        @Test
        void updateState_invalidCode() {
            //given
            final Long recruitId = 1L;
            final int statusCode = 4;

            Recruitment basicRecruit = PROGRAMMING_POST.생성(PINGU.생성(),
                    BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                    List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성()));
            given(recruitmentRepository.findById(recruitId))
                    .willReturn(Optional.of(basicRecruit));

            //when & then
            assertAll(
                    () -> assertThatThrownBy(() -> recruitmentService.updateStatus(recruitId, statusCode))
                            .isExactlyInstanceOf(InvalidCodeException.class),
                    () -> verify(recruitmentRepository).findById(recruitId)
            );
        }

        @DisplayName("delete - 모집글 삭제에 성공한다.")
        @Test
        void delete() {
            //given
            final Long recruitId = 3L;
            Recruitment basicRecruit = PROGRAMMING_POST.생성(PINGU.생성(),
                    BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                    List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성()));

            given(recruitmentRepository.findById(recruitId))
                    .willReturn(Optional.of(basicRecruit));

            //when
            Long delete = recruitmentService.delete(recruitId);

            //then
            assertAll(
                    () -> assertThat(delete).isEqualTo(recruitId),
                    () -> verify(recruitmentRepository).findById(recruitId),
                    () -> verify(recruitmentRepository).delete(basicRecruit)
            );
        }

        @DisplayName("delete - 모집글 삭제에 실패한다. (잘못된 ID)")
        @Test
        void deleteFail() {
            //given
            final Long invalidId = 999L;
            Recruitment basicRecruit = PROGRAMMING_POST.생성(PINGU.생성(),
                    BACKEND_TAG.생성().stream().map(RecruitTag::new).toList(),
                    List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성()));

            given(recruitmentRepository.findById(invalidId))
                    .willThrow(EntityNotFoundException.class);

            //when & then
            assertAll(
                    () -> assertThatThrownBy(()-> recruitmentService.delete(invalidId))
                            .isExactlyInstanceOf(EntityNotFoundException.class),
                    () -> verify(recruitmentRepository).findById(invalidId),
                    () -> verify(recruitmentRepository, times(0)).delete(basicRecruit)
            );
        }
    }
}