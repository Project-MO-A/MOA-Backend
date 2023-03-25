package com.moa.domain.member;

import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.recruit.RecruitApplyRequest;
import com.moa.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.moa.InitData.*;
import static com.moa.domain.member.ApprovalStatus.*;
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
    RecruitMemberService recruitMemberService;
    @Autowired
    ApplimentMemberRepository applimentMemberRepository;

    @BeforeEach
    void setUpApply() {
        RecruitApplyRequest backend = new RecruitApplyRequest(RECRUITMENT1.getId(), "백엔드", USER1.getId());
        RecruitApplyRequest front = new RecruitApplyRequest(RECRUITMENT1.getId(), "프론트엔드", USER2.getId());
        recruitMemberService.applyMember(backend);
        recruitMemberService.applyMember(front);
    }

    @AfterEach
    void tearDown() {
        applimentMemberRepository.deleteAll();
    }

    @DisplayName("getAppliment - 모든 신청 멤버 목록 조회 (승인, 대기중, 거절)")
    @Test
    void getAppliment() {
        //when
        List<ApplimentMemberResponse> appliment = adminRepository.findAllApplimentResponse(1L, null);

        //then
        assertThat(appliment.get(0).recruitField()).isEqualTo("백엔드");
        assertThat(appliment.get(1).recruitField()).isEqualTo("프론트엔드");
        assertThat(appliment.get(0).userId()).isEqualTo(1L);
        assertThat(appliment.get(1).userId()).isEqualTo(2L);
    }

    @DisplayName("getAppliment - 신청 멤버 목록 조회 (대기)")
    @Test
    void getApplimentOnStatusPENDING() {
        //when
        List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentResponse(1L, PENDING);

        //then
        assertThat(pendingAppliment.get(0).status()).isEqualTo(PENDING);
        assertThat(pendingAppliment.get(1).status()).isEqualTo(PENDING);
        assertThat(pendingAppliment.get(0).recruitField()).isEqualTo("백엔드");
        assertThat(pendingAppliment.get(1).recruitField()).isEqualTo("프론트엔드");
        assertThat(pendingAppliment.get(0).userId()).isEqualTo(1L);
        assertThat(pendingAppliment.get(1).userId()).isEqualTo(2L);
    }

    @DisplayName("getAppliment - 신청 멤버 목록 조회 (승인)")
    @Test
    void getApplimentOnStatusAPPROVED() {
        //given
        Long applyId = applimentMemberRepository.findAllByUserId(USER1.getId()).get(0).getId();
        adminService.changeApplimentStatus(applyId, APPROVED);

        //when
        List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentResponse(1L, APPROVED);

        //then
        assertThat(pendingAppliment.get(0).status()).isEqualTo(APPROVED);
        assertThat(pendingAppliment.get(0).recruitField()).isEqualTo("백엔드");
    }

    @DisplayName("getAppliment - 신청 멤버 목록 조회 (거절)")
    @Test
    void getApplimentOnStatusREFUSE() {
        //given
        Long applyId = applimentMemberRepository.findAllByUserId(USER1.getId()).get(0).getId();
        adminService.changeApplimentStatus(applyId, REFUSE);

        //when
        List<ApplimentMemberResponse> pendingAppliment = adminRepository.findAllApplimentResponse(1L, REFUSE);

        //then
        assertThat(pendingAppliment.get(0).status()).isEqualTo(REFUSE);
        assertThat(pendingAppliment.get(0).recruitField()).isEqualTo("백엔드");
    }

}