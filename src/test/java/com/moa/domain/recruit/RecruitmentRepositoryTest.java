package com.moa.domain.recruit;

import com.moa.base.RepositoryTestCustom;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.moa.support.fixture.RecruitMemberFixture.*;
import static com.moa.support.fixture.RecruitmentFixture.PROGRAMMING_POST;
import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.UserFixture.KAI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecruitmentRepositoryTest extends RepositoryTestCustom {

    private static Recruitment RECRUITMENT;
    private static User USER;

    @BeforeEach
    void setUp() {
        // 유저
        USER = KAI.생성();
        userRepository.saveAll(List.of(USER));

        // 태그
        List<Tag> tags = BACKEND_TAG.생성();
        tagRepository.saveAll(tags);
        List<RecruitTag> recruitTags = tags.stream().map(RecruitTag::new).toList();
        List<RecruitTag> recruitTags1 = tags.stream().map(RecruitTag::new).toList();

        // 모집 멤버
        RecruitMember backendMember = BACKEND_MEMBER.생성();
        RecruitMember frontMember = FRONTEND_MEMBER.생성();
        List<RecruitMember> recruitMembers = List.of(backendMember, frontMember);

        RecruitMember member1 = BACKEND_MEMBER.생성();
        RecruitMember member2 = PM_MEMBER.생성();
        RecruitMember member3 = DESIGNER_MEMBER.생성();
        List<RecruitMember> recruitMembers1 = List.of(member1, member2, member3);

        // 모집 글 2개 생성
        RECRUITMENT = PROGRAMMING_POST.생성(USER, recruitTags, recruitMembers);
        recruitmentRepository.save(RECRUITMENT);

        Recruitment recruitment2 = PROGRAMMING_POST.생성(USER, recruitTags1, recruitMembers1);
        recruitmentRepository.save(recruitment2);

        em.flush();
        em.clear();
    }

    @DisplayName("findByIdFetchUser - 페치 조인으로 모집글을 조회 한다.")
    @Test
    void findByIdFetchUser() {
        //given
        Post post = RECRUITMENT.getPost();

        //when
        Recruitment recruitment = recruitmentRepository.findByIdFetchUser(RECRUITMENT.getId()).get();

        //then
        assertAll(
                () -> assertThat(recruitment).isNotNull(),
                () -> assertThat(recruitment.getPost().getContent()).isEqualTo(post.getContent()),
                () -> assertThat(recruitment.getPost().getTitle()).isEqualTo(post.getTitle())
        );
    }

    @DisplayName("findByIdFetchUser - 존재하지 않는 아이디를 입력하면 Optional.empty 가 반환된다.")
    @Test
    void findByIdFetchUserFail() {
        //given
        Long invalidRecruitmentId = 100L;

        //when
        Optional<Recruitment> recruitment = recruitmentRepository.findByIdFetchUser(invalidRecruitmentId);

        //then
        assertThat(recruitment).isEmpty();
    }

    @DisplayName("findListByIdFetchUser - 유저가 작성한 모집글을 전부 조회 한다.")
    @Test
    void findListByIdFetchUser() {
        //given
        Post post = RECRUITMENT.getPost();

        //when
        List<Recruitment> recruitments = recruitmentRepository.findListByIdFetchUser(USER.getId());

        //then
        assertAll(
                () -> assertThat(recruitments.size()).isEqualTo(2),
                () -> assertThat(recruitments.get(0).getPost().getTitle()).isEqualTo(post.getTitle())
        );
    }

    @DisplayName("findListByIdFetchUser - 존재하지 않는 아이디를 입력하면 빈 리스트가 반환된다.")
    @Test
    void findListByIdFetchUserFail() {
        //given
        Long invalidUserId = 99L;

        //when
        List<Recruitment> recruitments = recruitmentRepository.findListByIdFetchUser(invalidUserId);

        //then
        assertThat(recruitments).isEmpty();
    }
}