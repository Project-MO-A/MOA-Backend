package com.moa.domain.possible;

import com.moa.base.RepositoryTestCustom;
import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.moa.support.fixture.ApplimentFixture.APPROVED_MEMBER;
import static com.moa.support.fixture.ApplimentFixture.LEADER_MEMBER;
import static com.moa.support.fixture.PossibleTimeFixture.*;
import static com.moa.support.fixture.RecruitMemberFixture.BACKEND_MEMBER;
import static com.moa.support.fixture.RecruitMemberFixture.FRONTEND_MEMBER;
import static com.moa.support.fixture.RecruitmentFixture.PROGRAMMING_POST;
import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PossibleTimeRepositoryTest extends RepositoryTestCustom {
    @Autowired
    private PossibleTimeRepository possibleTimeRepository;

    private ApplimentMember APPLIMENT_JHS;

    @BeforeEach
    void setUp() {
        // 유저
        final User AUTHOR_KAI = KAI.생성();
        User jhs = JHS.생성();
        User eunseo = EUNSEO.생성();
        User pingu = PINGU.생성();
        userRepository.saveAll(List.of(AUTHOR_KAI, jhs, eunseo, pingu));

        // 태그
        List<Tag> tags = BACKEND_TAG.생성();
        tagRepository.saveAll(tags);
        List<RecruitTag> recruitTags = tags.stream().map(RecruitTag::new).toList();

        // 모집 멤버
        RecruitMember backendMember = BACKEND_MEMBER.생성();
        RecruitMember frontMember = FRONTEND_MEMBER.생성();
        List<RecruitMember> recruitMembers = List.of(backendMember, frontMember);

        // 모집 글
        Recruitment recruitment = PROGRAMMING_POST.생성(AUTHOR_KAI, recruitTags, recruitMembers);
        recruitmentRepository.save(recruitment);

        // 신청자
        ApplimentMember leader = LEADER_MEMBER.작성자_생성(AUTHOR_KAI, recruitment);
        APPLIMENT_JHS = APPROVED_MEMBER.생성(jhs, backendMember);
        applimentMemberRepository.saveAll(List.of(leader, APPLIMENT_JHS,
                APPROVED_MEMBER.생성(eunseo, frontMember),
                APPROVED_MEMBER.생성(pingu, frontMember)));

        em.flush();
        em.clear();
    }

    @DisplayName("멤버의 가능 시간을 전부 조회한다.")
    @Test
    void findAllByApplimentMemberId() {
        //given
        PossibleTime time = TIME1.생성(APPLIMENT_JHS);
        PossibleTime time1 = TIME3.생성(APPLIMENT_JHS);
        PossibleTime time2 = TIME5.생성(APPLIMENT_JHS);
        PossibleTime time3 = TIME7.생성(APPLIMENT_JHS);

        possibleTimeRepository.saveAll(List.of(
                time, time1, time2, time3
        ));

        //when
        List<PossibleTime> allTime = possibleTimeRepository.findAllByApplimentMemberId(APPLIMENT_JHS.getId());

        //then
        assertAll(
                () -> assertThat(allTime.size()).isEqualTo(4),
                () -> assertThat(allTime).containsOnly(time, time1, time2, time3)
        );
    }

    @DisplayName("잘못된 ApplimentMemberId 를 입력받으면 빈 리스트가 반환된다.")
    @Test
    void findAllByApplimentMemberIdFail() {
        //given
        PossibleTime time = TIME1.생성(APPLIMENT_JHS);
        PossibleTime time1 = TIME3.생성(APPLIMENT_JHS);
        PossibleTime time2 = TIME5.생성(APPLIMENT_JHS);
        PossibleTime time3 = TIME7.생성(APPLIMENT_JHS);

        possibleTimeRepository.saveAll(List.of(
                time, time1, time2, time3
        ));

        //when
        List<PossibleTime> allTime = possibleTimeRepository.findAllByApplimentMemberId(100L);

        //then
        assertThat(allTime).isEmpty();
    }

    @DisplayName("참여 멤버의 모든 시간이 삭제된다.")
    @Test
    void deleteAllByApplimentMember() {
        //given
        PossibleTime time = TIME1.생성(APPLIMENT_JHS);
        PossibleTime time1 = TIME3.생성(APPLIMENT_JHS);
        PossibleTime time2 = TIME5.생성(APPLIMENT_JHS);
        PossibleTime time3 = TIME7.생성(APPLIMENT_JHS);

        possibleTimeRepository.saveAll(List.of(
                time, time1, time2, time3
        ));

        //when
        possibleTimeRepository.deleteAllByApplimentMember(APPLIMENT_JHS);
        List<PossibleTime> allTime = possibleTimeRepository.findAllByApplimentMemberId(100L);

        //then
        assertThat(allTime).isEmpty();
    }
}