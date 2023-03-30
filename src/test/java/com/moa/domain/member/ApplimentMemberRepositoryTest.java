package com.moa.domain.member;

import com.moa.base.RepositoryTestCustom;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.moa.domain.member.ApprovalStatus.APPROVED;
import static com.moa.support.fixture.ApplimentFixture.APPROVED_MEMBER;
import static com.moa.support.fixture.ApplimentFixture.LEADER_MEMBER;
import static com.moa.support.fixture.RecruitMemberFixture.*;
import static com.moa.support.fixture.RecruitmentFixture.PROGRAMMING_POST;
import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ApplimentMemberRepositoryTest extends RepositoryTestCustom {
    private static User AUTHOR_KAI;
    private static User USER_JHS;
    private static Recruitment RECRUITMENT;

    @BeforeEach
    void setUp() {
        // 유저
        AUTHOR_KAI = KAI.생성();
        USER_JHS = JHS.생성();
        User eunseo = EUNSEO.생성();
        User pingu = PINGU.생성();
        userRepository.saveAll(List.of(AUTHOR_KAI, USER_JHS, eunseo, pingu));

        // 태그
        List<Tag> tags = BACKEND_TAG.생성();
        tagRepository.saveAll(tags);
        List<RecruitTag> recruitTags = tags.stream().map(RecruitTag::new).toList();
        List<RecruitTag> recruitTags1 = tags.stream().map(RecruitTag::new).toList();

        // 모집 멤버
        RecruitMember backendMember = BACKEND_MEMBER.생성();
        RecruitMember frontMember = FRONTEND_MEMBER.생성();
        List<RecruitMember> recruitMembers = List.of(backendMember, frontMember);
        List<RecruitMember> recruitMembers1 = List.of(BACKEND_MEMBER.생성(), PM_MEMBER.생성(), DESIGNER_MEMBER.생성());

        // 모집 글 2개 생성
        RECRUITMENT = PROGRAMMING_POST.생성(AUTHOR_KAI, recruitTags, recruitMembers);
        recruitmentRepository.save(RECRUITMENT);

        Recruitment recruitment2 = PROGRAMMING_POST.생성(AUTHOR_KAI, recruitTags1, recruitMembers1);
        recruitmentRepository.save(recruitment2);

        // 신청자
        ApplimentMember leader = LEADER_MEMBER.작성자_생성(AUTHOR_KAI, RECRUITMENT);
        ApplimentMember leader2 = LEADER_MEMBER.작성자_생성(AUTHOR_KAI, recruitment2);

        ApplimentMember front_eunseo = APPROVED_MEMBER.생성(eunseo, frontMember);
        ApplimentMember front_pingu = APPROVED_MEMBER.생성(pingu, frontMember);
        ApplimentMember backend_jhs = APPROVED_MEMBER.생성(USER_JHS, backendMember);
        applimentMemberRepository.saveAll(List.of(leader, leader2, backend_jhs, front_eunseo, front_pingu));

        em.flush();
        em.clear();
    }

    @DisplayName("findAllRecruitmentByUserId - 유저와 연관된 참여 엔티티가 반환된다.")
    @Test
    void findAllRecruitmentByUserId() {
        //when
        List<ApplimentMember> allAppliment = applimentMemberRepository.findAllRecruitmentByUserId(AUTHOR_KAI.getId());

        //then
        assertAll(
                () -> assertThat(allAppliment.size()).isEqualTo(2),
                () -> assertThat(allAppliment.get(0).getRecruitMember().getRecruitField())
                        .isEqualTo("LEADER")
        );
    }

    @DisplayName("findAllRecruitmentByUserId - 잘못된 userId를 입력할 경우 빈 리스트가 반환된다")
    @Test
    void findAllRecruitmentByUserIdEmpty() {
        //given
        Long InvalidId = 100L;

        //when
        List<ApplimentMember> allAppliment = applimentMemberRepository.findAllRecruitmentByUserId(InvalidId);

        //then
        assertThat(allAppliment).isEmpty();
    }

    @DisplayName("findAllByUserId - 유저와 연관된 참여 엔티티가 반환된다.")
    @Test
    void findAllByUserId() {
        //when
        List<ApplimentMember> allAppliment = applimentMemberRepository.findAllRecruitmentByUserId(AUTHOR_KAI.getId());

        //then
        assertAll(
                () -> assertThat(allAppliment.size()).isEqualTo(2),
                () -> assertThat(allAppliment.get(0).getRecruitMember().getRecruitField())
                        .isEqualTo("LEADER")
        );
    }

    @DisplayName("findByRecruitIdAndUserId - 신청 멤버 엔티티가 반환된다.")
    @Test
    void findByRecruitIdAndUserId() {
        //when
        ApplimentMember jhs_appliment = applimentMemberRepository.findByRecruitIdAndUserId(RECRUITMENT.getId(), USER_JHS.getId()).get();

        //then
        assertAll(
                () -> assertThat(jhs_appliment).isNotNull(),
                () -> assertThat(jhs_appliment.getStatus()).isEqualTo(APPROVED),
                () -> assertThat(jhs_appliment.getRecruitMember().getRecruitField()).isEqualTo("백엔드")
        );
    }

    @DisplayName("findByRecruitIdAndUserId - 잘못된 모집글 ID를 입력할 경우 Optional.emtpy() 가 반환된다")
    @Test
    void findByRecruitIdAndUserId_InvalidRecruitmentID() {
        //given
        Long invalidRecruitmentId = 100L;

        //when
        Optional<ApplimentMember> jhs_appliment = applimentMemberRepository.findByRecruitIdAndUserId(invalidRecruitmentId, USER_JHS.getId());

        //then
        assertThat(jhs_appliment).isEmpty();
    }

    @DisplayName("findByRecruitIdAndUserId - 잘못된 유저 ID를 입력할 경우 Optional.emtpy() 가 반환된다")
    @Test
    void findByRecruitIdAndUserId_InvalidUserId() {
        //given
        Long invalidUserId = 100L;

        //when
        Optional<ApplimentMember> jhs_appliment = applimentMemberRepository.findByRecruitIdAndUserId(RECRUITMENT.getId(), invalidUserId);

        //then
        assertThat(jhs_appliment).isEmpty();
    }
}