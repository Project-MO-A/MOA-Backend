package com.moa.domain.member;

import com.moa.domain.notice.Notice;
import com.moa.domain.notice.NoticeRepository;
import com.moa.domain.notice.Post;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.moa.InitData.*;
import static com.moa.domain.member.ApprovalStatus.*;
import static com.moa.domain.member.Attendance.ATTENDANCE;
import static com.moa.domain.member.Attendance.NONATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("data")
@Transactional
@SpringBootTest
class AdminRepositoryTest {
    @Autowired
    AdminRepository adminRepository;
    @Autowired
    AdminService adminService;

    @Autowired
    ApplimentMemberRepository applimentMemberRepository;
    @Autowired
    RecruitMemberRepository recruitMemberRepository;
    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    AttendMemberRepository attendMemberRepository;

    @DisplayName("ApplimentMemberResponse - 신청 멤버 테스트")
    @Nested
    class Appliment {
        @BeforeEach
        void setUpApply() {
            RecruitMember backend = recruitMemberRepository.findByRecruitFieldAndRecruitmentId("백엔드", RECRUITMENT1.getId());
            RecruitMember front = recruitMemberRepository.findByRecruitFieldAndRecruitmentId("프론트엔드", RECRUITMENT1.getId());
            applimentMemberRepository.save(new ApplimentMember(backend, USER3, PENDING));
            applimentMemberRepository.save(new ApplimentMember(front, USER2, PENDING));
        }

        @DisplayName("findAllApplimentResponse - 모든 신청 멤버 목록 조회 (승인, 대기중, 거절)")
        @Test
        void getAppliment() {
            //when
            List<ApplimentMemberResponse> appliment = adminRepository.findAllApplimentMembers(1L, null);

            //then
            assertThat(appliment.get(1).recruitField()).isEqualTo("백엔드");
            assertThat(appliment.get(2).recruitField()).isEqualTo("프론트엔드");
        }

        @DisplayName("findAllApplimentResponse - 신청 멤버 목록 조회 (대기)")
        @Test
        void getApplimentOnStatusPENDING() {
            //when
            List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentMembers(1L, PENDING);

            //then
            assertThat(pendingAppliment.get(0).status()).isEqualTo(PENDING);
            assertThat(pendingAppliment.get(1).status()).isEqualTo(PENDING);
            assertThat(pendingAppliment.get(0).recruitField()).isEqualTo("백엔드");
            assertThat(pendingAppliment.get(1).recruitField()).isEqualTo("프론트엔드");
            assertThat(pendingAppliment.get(0).userId()).isEqualTo(1L);
            assertThat(pendingAppliment.get(1).userId()).isEqualTo(3L);
        }

        @DisplayName("findAllApplimentResponse - 신청 멤버 목록 조회 (승인)")
        @Test
        void getApplimentOnStatusAPPROVED() {
            //given
            Long applyId = applimentMemberRepository.findAllByUserId(USER3.getId()).get(0).getId();
            adminService.changeApplimentStatus(applyId, APPROVED);

            //when
            List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentMembers(1L, APPROVED);

            //then
            assertThat(pendingAppliment.get(1).status()).isEqualTo(APPROVED);
        }

        @DisplayName("findAllApplimentResponse - 신청 멤버 목록 조회 (거절)")
        @Test
        void getApplimentOnStatusREFUSE() {
            //given
            Long applyId = applimentMemberRepository.findAllByUserId(USER3.getId()).get(0).getId();
            adminService.changeApplimentStatus(applyId, REFUSE);

            //when
            List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentMembers(1L, REFUSE);

            //then
            assertThat(pendingAppliment.get(0).status()).isEqualTo(REFUSE);
            assertThat(pendingAppliment.get(0).recruitField()).isEqualTo("백엔드");
        }
    }

    @DisplayName("ApprovedMemberResponse - 승인된 멤버")
    @Nested
    class Approve {
        @BeforeEach
        void setUpApply() {
            RecruitMember backend = recruitMemberRepository.findByRecruitFieldAndRecruitmentId("백엔드", RECRUITMENT1.getId());
            RecruitMember front = recruitMemberRepository.findByRecruitFieldAndRecruitmentId("프론트엔드", RECRUITMENT1.getId());
            applimentMemberRepository.save(new ApplimentMember(backend, USER3, APPROVED));
            applimentMemberRepository.save(new ApplimentMember(front, USER2, APPROVED));
        }

        @DisplayName("findAllApprovedResponse - 승인된 멤버 조회")
        @Test
        void getApproved() {
            //when
            List<ApprovedMemberResponse> approvedMember = adminRepository.findAllApprovedMembers(RECRUITMENT1.getId());

            //then
            assertThat(approvedMember.size()).isEqualTo(3);
            assertThat(approvedMember.get(0).getNickname()).isEqualTo(USER1.getNickname());
            assertThat(approvedMember.get(0).getPopularity()).isEqualTo(0);
        }

        @DisplayName("참여도 조회 테스트")
        @Nested
        class CountElement {

            @BeforeEach
            void setUp() {
                //given
                Notice noticeVote1 = Notice.builder()
                        .recruitment(RECRUITMENT1)
                        .post(new Post("공지1", "content"))
                        .checkVote(true)
                        .confirmedLocation("강남 스터디카페")
                        .confirmedTime(LocalDateTime.now())
                        .build();
                noticeRepository.save(noticeVote1);
                Notice noticeVote2 = Notice.builder()
                        .recruitment(RECRUITMENT1)
                        .post(new Post("공지1", "content"))
                        .checkVote(true)
                        .confirmedLocation("강남 스터디카페")
                        .confirmedTime(LocalDateTime.now())
                        .build();
                noticeRepository.save(noticeVote2);
                Notice notice = Notice.builder()
                        .recruitment(RECRUITMENT1)
                        .post(new Post("공지1", "content"))
                        .checkVote(false)
                        .confirmedLocation("강남 스터디카페")
                        .confirmedTime(LocalDateTime.now())
                        .build();
                noticeRepository.save(notice);

                AttendMember USER3Attend = AttendMember.builder()
                        .attendance(ATTENDANCE)
                        .notice(noticeVote1)
                        .user(USER3)
                        .build();
                AttendMember USER3Attend2 = AttendMember.builder()
                        .attendance(NONATTENDANCE)
                        .notice(noticeVote1)
                        .user(USER3)
                        .build();

                AttendMember User2Attend = AttendMember.builder()
                        .attendance(ATTENDANCE)
                        .notice(noticeVote1)
                        .user(USER2)
                        .build();
                AttendMember User2Attend2 = AttendMember.builder()
                        .attendance(ATTENDANCE)
                        .notice(noticeVote1)
                        .user(USER2)
                        .build();

                attendMemberRepository.save(USER3Attend);
                attendMemberRepository.save(USER3Attend2);
                attendMemberRepository.save(User2Attend);
                attendMemberRepository.save(User2Attend2);
            }

            @DisplayName("countTotalVote - 투표 공지사항 개수를 가져온다.")
            @Test
            void countTotalVote() {
                //when
                Long countTotalVote = adminRepository.countAttend(RECRUITMENT1.getId(), USER3.getId(), null);

                //then
                assertThat(countTotalVote).isEqualTo(2);
            }

            @DisplayName("countAttend - 개인의 참여 횟수를 가져온다.")
            @Test
            void countAttend() {
                //when
                Long countAttend1 = adminRepository.countAttend(RECRUITMENT1.getId(), USER3.getId(), ATTENDANCE);
                Long countAttend2 = adminRepository.countAttend(RECRUITMENT1.getId(), USER2.getId(), ATTENDANCE);

                //then
                assertThat(countAttend1).isEqualTo(1);
                assertThat(countAttend2).isEqualTo(2);
            }
        }
    }
}