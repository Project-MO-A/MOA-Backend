package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentSearchRepository;
import com.moa.domain.member.ApprovalStatus;
import com.moa.domain.member.RecruitMember;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.dto.member.ApprovedPopularityRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.moa.constant.TestConst.USER;
import static com.moa.domain.member.ApprovalStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class AdminServiceTest {
    static AdminService adminService;

    @BeforeAll
    static void setUp() {
        adminService = new AdminService(new TestApplimentSearchRepository());
    }

    @DisplayName("getApplimentMembers - 신청한 멤버를 조회한다. (대기)")
    @Test
    void getApplimentMembersPending() {
        //when
        List<ApplimentMemberResponse> applimentMembers = adminService.getApplimentMembers(1L, PENDING);

        //then
        assertAll(
                () ->assertThat(applimentMembers.size()).isEqualTo(2),
                () ->assertThat(applimentMembers.get(0).status()).isEqualTo(PENDING),
                () ->assertThat(applimentMembers.get(1).status()).isEqualTo(PENDING)
        );
    }

    @DisplayName("getApplimentMembers - 신청한 멤버를 조회한다. (강퇴)")
    @Test
    void getApplimentMembersKick() {
        //when
        List<ApplimentMemberResponse> applimentMembers = adminService.getApplimentMembers(1L, KICK);

        //then
        assertAll(
                () -> assertThat(applimentMembers.size()).isEqualTo(2),
                () -> assertThat(applimentMembers.get(0).status()).isEqualTo(KICK),
                () -> assertThat(applimentMembers.get(1).status()).isEqualTo(KICK)
        );
    }

    @DisplayName("getApplimentMembers - 신청한 멤버를 조회하는데 실패한다. (잘못된 모집글 ID)")
    @Test
    void getApplimentMembersFail() {
        //when & then
        assertThatThrownBy(() -> adminService.getApplimentMembers(10L, KICK))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("getApprovedMembers - 승인된 멤버들을 조회한다.")
    @Test
    void getApprovedMembers() {
        //when
        List<ApprovedMemberResponse> approvedMembers = adminService.getApprovedMembers(1L);

        //then
        assertAll(
                () -> assertThat(approvedMembers.size()).isEqualTo(2),
                () -> assertThat(approvedMembers.get(0).getPopularity()).isGreaterThan(1.0),
                () -> assertThat(approvedMembers.get(0).getTotalAttend()).isGreaterThan(1L)
        );
    }

    @DisplayName("getApprovedMembers - 승인된 멤버들을 조회하는데 실패한다. (잘못된 모집글 ID)")
    @Test
    void getApprovedMembersFail() {
        //when & then
        assertThatThrownBy(() -> adminService.getApprovedMembers(10L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("changeApplimentStatus - 신청 멤버 상태 변경에 성공한다.")
    @Test
    void changeApplimentStatus() {//when
        String status = adminService.changeApplimentStatus(1L, APPROVED);

        //then
        assertThat(status).isEqualTo(APPROVED.name());
    }

    @DisplayName("changeApplimentStatus - 신청 멤버 상태 변경에 실패한다. (잘못된 모집글 ID)")
    @Test
    void changeApplimentStatusFail() {
        //when & then
        assertThatThrownBy(() -> adminService.changeApplimentStatus(10L, APPROVED))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("setApprovedPopularity - 신청 멤버 상태 변경에 성공한다.")
    @Test
    void setApprovedPopularity() {//when
        double popularity = adminService.setApprovedPopularity(1L, 3.5);

        //then
        assertThat(popularity).isEqualTo(3.5);
    }

    @DisplayName("setApprovedPopularity - 신청 멤버 상태 변경에 실패한다. (잘못된 모집글 ID)")
    @Test
    void setApprovedPopularityFail() {
        //when & then
        assertThatThrownBy(() -> adminService.setApprovedPopularity(10L, 3.5))
                .isInstanceOf(EntityNotFoundException.class);
    }

    static class TestApplimentSearchRepository implements ApplimentSearchRepository {
        @Override
        public Optional<ApplimentMember> findApplimentMemberById(Long applyId) {
            if (applyId == 10L) return Optional.empty();
            RecruitMember recruitMember = RecruitMember.builder()
                    .recruitField("백엔드")
                    .recruitment(null)
                    .totalRecruitCount(5)
                    .build();
            recruitMember.addCount();

            return Optional.ofNullable(ApplimentMember.builder()
                    .status(APPROVED)
                    .recruitMember(recruitMember)
                    .user(USER)
                    .build());
        }

        @Override
        public List<ApplimentMemberResponse> findAllApplimentMembers(Long recruitmentId, ApprovalStatus status) {
            if (recruitmentId == 10L) return new ArrayList<>();
            List<ApplimentMemberResponse> memberResponses = new ArrayList<>();
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(1L)
                    .applyId(1L)
                    .nickname("nickname1")
                    .recruitField("백엔드")
                    .status(status)
                    .build());
            memberResponses.add(ApplimentMemberResponse.builder()
                    .userId(2L)
                    .applyId(2L)
                    .nickname("nickname2")
                    .recruitField("프론트엔드")
                    .status(status)
                    .build());

            return memberResponses;
        }

        @Override
        public List<ApprovedMemberResponse> findAllApprovedMembers(Long recruitmentId) {
            if (recruitmentId == 10L) return new ArrayList<>();
            List<ApprovedMemberResponse> memberResponses = new ArrayList<>();
            ApprovedMemberResponse response1 = new ApprovedMemberResponse(1L, 1L, "nickname1", "백엔드", 3.5);
            response1.setTotalAttend(4L);
            response1.setAttend(3L);
            memberResponses.add(response1);

            ApprovedMemberResponse response2 = new ApprovedMemberResponse(2L, 2L, "nickname2", "프론트엔드", 4.5);
            response2.setTotalAttend(4L);
            response2.setAttend(2L);
            memberResponses.add(response2);

            return memberResponses;
        }
    }
}