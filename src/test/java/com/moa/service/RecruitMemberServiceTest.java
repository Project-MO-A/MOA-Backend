package com.moa.service;

import com.moa.base.AbstractServiceTest;
import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import com.moa.dto.recruit.RecruitApplyRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static com.moa.domain.member.ApprovalStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class RecruitMemberServiceTest extends AbstractServiceTest {

    @InjectMocks
    private RecruitMemberService service;

    @Test
    @DisplayName("회원 참여 신청")
    void applyMember() {
        //given
        Recruitment recruitment = Recruitment.builder().build();
        RecruitMember recruitMember = RecruitMember.builder().recruitment(recruitment).recruitField("backend").build();
        User user = User.builder().build();
        ApplimentMember applimentMember = new ApplimentMember(recruitMember, user, PENDING);

        given(applimentMemberRepository.save(any())).willReturn(applimentMember);
        //when
        RecruitApplyRequest request = new RecruitApplyRequest(recruitment.getId(), recruitMember.getRecruitField(), user.getId());
        String status = service.applyMember(request);

        //then
        assertThat(status).isEqualTo(applimentMember.getStatus().name());
    }
}