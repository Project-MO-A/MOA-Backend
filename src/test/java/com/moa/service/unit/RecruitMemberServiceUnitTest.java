package com.moa.service.unit;

import com.moa.base.AbstractServiceTest;
import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import com.moa.dto.recruit.RecruitApplyRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.service.RecruitMemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static com.moa.domain.member.ApprovalStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class RecruitMemberServiceUnitTest extends AbstractServiceTest {

    @InjectMocks
    private RecruitMemberService service;

    @Test
    @DisplayName("모집글에 참여 신청에 성공한다")
    void successApplyMember() {
        //given
        Recruitment recruitment = Recruitment.builder().build();
        User user = User.builder().build();
        RecruitMember recruitMember = RecruitMember.builder()
                .recruitment(recruitment)
                .recruitField("backend")
                .build();
        ApplimentMember applimentMember = new ApplimentMember(recruitMember, user, PENDING);

        given(recruitMemberRepository.findByRecruitFieldAndRecruitmentId("backend", 1L)).willReturn(Optional.of(recruitMember));
        given(userRepository.getReferenceById(1L)).willReturn(user);
        given(applimentMemberRepository.save(any(ApplimentMember.class))).willReturn(applimentMember);

        //when
        RecruitApplyRequest request = new RecruitApplyRequest(1L, recruitMember.getRecruitField(), 1L);
        String status = service.applyMember(request);

        //then
        assertAll(
                () -> assertThat(status).isEqualTo("PENDING"),
                () -> verify(recruitMemberRepository).findByRecruitFieldAndRecruitmentId("backend", 1L),
                () -> verify(userRepository).getReferenceById(1L),
                () -> verify(applimentMemberRepository).save(any(ApplimentMember.class))
        );
    }

    @Test
    @DisplayName("모집글 번호와 모집 분야가 일치하지 않아 신청에 실패한다")
    void failApplyMember() {
        //given
        given(recruitMemberRepository.findByRecruitFieldAndRecruitmentId("backend", 1L))
                .willThrow(EntityNotFoundException.class);

        //when & then
        RecruitApplyRequest request = new RecruitApplyRequest(1L, "backend", 1L);

        assertAll(
                () -> assertThatThrownBy(() -> service.applyMember(request))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(recruitMemberRepository).findByRecruitFieldAndRecruitmentId("backend", 1L),
                () -> verify(userRepository, never()).getReferenceById(1L),
                () -> verify(applimentMemberRepository, never()).save(any(ApplimentMember.class))
        );
    }
}