package com.moa.domain.member;

import com.moa.base.RepositoryTestCustom;
import com.moa.domain.notice.Notice;
import com.moa.domain.notice.NoticeRepository;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.User;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.support.fixture.RecruitMemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.moa.domain.member.ApprovalStatus.*;
import static com.moa.domain.member.Attendance.ATTENDANCE;
import static com.moa.support.fixture.ApplimentFixture.*;
import static com.moa.support.fixture.AttendMemberFixture.ATTENDANCE_MEMBER;
import static com.moa.support.fixture.AttendMemberFixture.NON_ATTENDANCE_MEMBER;
import static com.moa.support.fixture.NoticeFixture.*;
import static com.moa.support.fixture.RecruitMemberFixture.FRONTEND_MEMBER;
import static com.moa.support.fixture.RecruitmentFixture.PROGRAMMING_POST;
import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AdminRepositoryTest extends RepositoryTestCustom {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private RecruitMemberRepository recruitMemberRepository;
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private AttendMemberRepository attendMemberRepository;

    @BeforeEach
    void setUp() {
        // 유저
        AUTHOR_KAI = KAI.생성();
        USER_JHS = JHS.생성();
        User eunseo = EUNSEO.생성();
        User pingu = PINGU.생성();
        userRepository.saveAll(List.of(AUTHOR_KAI, USER_JHS, eunseo, pingu));
        em.flush();

        // 태그
        List<Tag> tags = BACKEND_TAG.생성();
        tagRepository.saveAll(tags);
        List<RecruitTag> recruitTags = tags.stream().map(RecruitTag::new).toList();

        // 모집 멤버
        BACKEND_MEMBER = RecruitMemberFixture.BACKEND_MEMBER.생성();
        RecruitMember frontMember = FRONTEND_MEMBER.생성();
        List<RecruitMember> recruitMembers = List.of(BACKEND_MEMBER, frontMember);

        // 모집 글 생성
        RECRUITMENT = PROGRAMMING_POST.생성(AUTHOR_KAI, recruitTags, recruitMembers);
        recruitmentRepository.save(RECRUITMENT);
        recruitMemberRepository.saveAll(recruitMembers);

        // 신청자
        LEADER = LEADER_MEMBER.작성자_생성(AUTHOR_KAI, RECRUITMENT);
        BACKEND_JHS = APPROVED_MEMBER.생성(USER_JHS, BACKEND_MEMBER);

        ApplimentMember front_eunseo = APPROVED_MEMBER.생성(eunseo, frontMember);
        ApplimentMember front_pingu = APPROVED_MEMBER.생성(pingu, frontMember);
        applimentMemberRepository.saveAll(List.of(LEADER, BACKEND_JHS, front_eunseo, front_pingu));

        em.flush();
    }

    @DisplayName("findApplimentMemberById - 신청 멤버가 엔티티가 조회된다.")
    @Test
    void findApplimentMemberById() {
        //given
        Long applyId = BACKEND_JHS.getId();

        //when
        ApplimentMember applimentMember = adminRepository.findApplimentMemberById(applyId).get();

        //then
        assertAll(
                () -> assertThat(applimentMember).isNotNull(),
                () -> assertThat(applimentMember.getUser().getName()).isEqualTo(USER_JHS.getName())
        );
    }

    @DisplayName("findApplimentMemberById - 잘못된 ID를 입력하면 Optional.empty() 가 반환된다.")
    @Test
    void findApplimentMemberByIdFail() {
        //given
        Long InvalidApplyId = 100L;

        //when
        Optional<ApplimentMember> applimentMember = adminRepository.findApplimentMemberById(InvalidApplyId);

        //then
        assertThat(applimentMember).isEmpty();
    }

    @DisplayName("잘못된 모집글 ID를 입력하면 Optional.empty() 가 반환된다.")
    @Test
    void findInvalidRecruitmentId() {
        //given
        Long InvalidRecruitmentId = 100L;

        //when
        Optional<Recruitment> recruitmentById = adminRepository.findRecruitmentById(100L);

        //then
        assertThat(recruitmentById).isEmpty();

    }

    @DisplayName("ApplimentMemberResponse - 신청 멤버 조회 테스트")
    @Nested
    class Appliment {
        @DisplayName("findAllApplimentResponse - 모든 신청 멤버 목록 조회 (승인, 대기중, 거절)")
        @Test
        void getAppliment() {
            //given
            ApplimentMember lionPending = PENDING_MEMBER.생성(LION.생성(), BACKEND_MEMBER);
            ApplimentMember tigerPending = REFUSE_MEMBER.생성(TIGER.생성(), BACKEND_MEMBER);
            applimentMemberRepository.saveAll(List.of(lionPending, tigerPending));

            //when
            List<ApplimentMemberResponse> appliment = adminRepository.findAllApplimentMembers(RECRUITMENT.getId(), null);

            //then
            assertThat(appliment.size()).isEqualTo(6);
        }

        @DisplayName("findAllApplimentResponse - 신청 멤버 목록 조회 (대기)")
        @Test
        void getApplimentOnStatusPENDING() {
            //given
            em.close();

            ApplimentMember lionPending = PENDING_MEMBER.생성(LION.생성(), BACKEND_MEMBER);
            ApplimentMember tigerPending = PENDING_MEMBER.생성(TIGER.생성(), BACKEND_MEMBER);
            applimentMemberRepository.saveAll(List.of(lionPending, tigerPending));

            //when
            List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentMembers(RECRUITMENT.getId(), PENDING);

            //then
            assertAll(
                    () -> assertThat(pendingAppliment.size()).isEqualTo(2),
                    () -> assertThat(pendingAppliment.get(0).status()).isEqualTo(PENDING),
                    () -> assertThat(pendingAppliment.get(1).status()).isEqualTo(PENDING)
            );
        }

        @DisplayName("findAllApplimentResponse - 신청 멤버 목록 조회 (승인)")
        @Test
        void getApplimentOnStatusAPPROVED() {
            //given
            ApplimentMember lionPending = PENDING_MEMBER.생성(LION.생성(), BACKEND_MEMBER);
            ApplimentMember tigerPending = PENDING_MEMBER.생성(TIGER.생성(), BACKEND_MEMBER);
            applimentMemberRepository.saveAll(List.of(lionPending, tigerPending));

            //when
            List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentMembers(RECRUITMENT.getId(), APPROVED);

            //then
            assertAll(
                    () -> assertThat(pendingAppliment.size()).isEqualTo(4),
                    () -> assertThat(pendingAppliment.get(0).status()).isEqualTo(APPROVED),
                    () -> assertThat(pendingAppliment.get(1).status()).isEqualTo(APPROVED),
                    () -> assertThat(pendingAppliment.get(2).status()).isEqualTo(APPROVED),
                    () -> assertThat(pendingAppliment.get(3).status()).isEqualTo(APPROVED)
            );
        }

        @DisplayName("findAllApplimentResponse - 신청 멤버 목록 조회 (거절)")
        @Test
        void getApplimentOnStatusREFUSE() {
            //given
            ApplimentMember lionPending = REFUSE_MEMBER.생성(LION.생성(), BACKEND_MEMBER);
            ApplimentMember tigerPending = REFUSE_MEMBER.생성(TIGER.생성(), BACKEND_MEMBER);
            applimentMemberRepository.saveAll(List.of(lionPending, tigerPending));

            //when
            List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentMembers(RECRUITMENT.getId(), REFUSE);

            //then
            assertAll(
                    () -> assertThat(pendingAppliment.size()).isEqualTo(2),
                    () -> assertThat(pendingAppliment.get(0).status()).isEqualTo(REFUSE),
                    () -> assertThat(pendingAppliment.get(1).status()).isEqualTo(REFUSE)
            );
        }

        @DisplayName("findAllApplimentResponse - 잘못된 ID가 입력되면 빈 리스트가 반환된다.")
        @Test
        void getApplimentOnStatusFail() {
            //given
            Long invalidId = 100L;

            //when
            List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentMembers(invalidId, null);

            //then
            assertThat(pendingAppliment).isEmpty();
        }
    }

    @DisplayName("ApprovedMemberResponse - 승인된 멤버 테스트")
    @Nested
    class Approve {
        @DisplayName("findAllApprovedResponse - 승인된 멤버가 조회된다.")
        @Test
        void getApproved() {
            //when
            List<ApprovedMemberResponse> approvedMember = adminRepository.findAllApprovedMembers(RECRUITMENT.getId());

            //then
            assertThat(approvedMember.size()).isEqualTo(4);
        }

        @DisplayName("findAllApprovedResponse - 잘못된 아이디를 입력하면 빈 리스트가 반환된다.")
        @Test
        void getApprovedEmpty() {
            //given
            Long invalidId = 99L;

            //when
            List<ApprovedMemberResponse> approvedMember = adminRepository.findAllApprovedMembers(invalidId);

            //then
            assertThat(approvedMember).isEmpty();
        }

        @DisplayName("참여도 조회 테스트")
        @Nested
        class CountElement {
            @BeforeEach
            void setUp() {
                //given
                Notice notice1 = FIRST_NOTICE.투표_공지_생성(RECRUITMENT);
                Notice notice2 = SECOND_NOTICE.투표_공지_생성(RECRUITMENT);
                Notice notice3 = THIRD_NOTICE.일반_공지_생성(RECRUITMENT);
                noticeRepository.saveAll(List.of(notice1, notice2, notice3));

                AttendMember attendMember1_1 = ATTENDANCE_MEMBER.생성(AUTHOR_KAI, notice1);
                AttendMember attendMember2_1 = ATTENDANCE_MEMBER.생성(AUTHOR_KAI, notice2);

                AttendMember attendMember1_2 = NON_ATTENDANCE_MEMBER.생성(USER_JHS, notice1);
                AttendMember attendMember2_2 = ATTENDANCE_MEMBER.생성(USER_JHS, notice2);
                attendMemberRepository.saveAll(List.of(
                        attendMember1_1,
                        attendMember1_2,
                        attendMember2_1,
                        attendMember2_2
                ));
            }

            @DisplayName("countTotalVote - 유저의 전체 참석수가 반환된다")
            @Test
            void countTotalVote() {
                //when
                Long countTotalVote = adminRepository.countAttend(RECRUITMENT.getId(), USER_JHS.getId(), null);

                //then
                assertThat(countTotalVote).isEqualTo(2);
            }

            @DisplayName("countTotalVote - 잘못된 모집글 ID를 입력하면 0이 반환된다.")
            @Test
            void countTotalVote_InvalidRecruitId() {
                //given
                Long invalidId = 99L;

                //when
                Long countTotalVote = adminRepository.countAttend(invalidId, USER_JHS.getId(), null);

                //then
                assertThat(countTotalVote).isEqualTo(0);
            }

            @DisplayName("countTotalVote - 잘못된 유저 ID를 입력하면 0이 반환된다.")
            @Test
            void countTotalVote_InvalidUserId() {
                //given
                Long invalidId = 99L;

                //when
                Long countTotalVote = adminRepository.countAttend(RECRUITMENT.getId(), invalidId, null);

                //then
                assertThat(countTotalVote).isEqualTo(0);
            }

            @DisplayName("countAttend - 개인의 참여 횟수를 가져온다.")
            @Test
            void countAttend() {
                //when
                Long countAttend1 = adminRepository.countAttend(RECRUITMENT.getId(), USER_JHS.getId(), ATTENDANCE);
                Long countAttend2 = adminRepository.countAttend(RECRUITMENT.getId(), AUTHOR_KAI.getId(), ATTENDANCE);

                //then
                assertThat(countAttend1).isEqualTo(1);
                assertThat(countAttend2).isEqualTo(2);
            }
        }
    }

    private static User AUTHOR_KAI;
    private static User USER_JHS;
    private static Recruitment RECRUITMENT;
    private static RecruitMember BACKEND_MEMBER;
    private static ApplimentMember LEADER;
    private static ApplimentMember BACKEND_JHS;
}