package com.moa.domain.recruit;

import com.moa.base.RepositoryTestCustom;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.moa.support.fixture.RecruitMemberFixture.*;
import static com.moa.support.fixture.RecruitmentFixture.PROGRAMMING_POST;
import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.UserFixture.KAI;

class RecruitmentSearchRepositoryTest extends RepositoryTestCustom {
    private User USER;

    @BeforeEach
    void setUp() {
        // 유저
        USER = KAI.생성();
        userRepository.saveAll(List.of(USER));

        // 태그
        List<Tag> tags = BACKEND_TAG.생성();
        tagRepository.saveAll(tags);

        for (int count = 0; count <= 20; count++) {
            List<RecruitTag> recruitTags = tags.stream().map(RecruitTag::new).toList();

            RecruitMember member1 = BACKEND_MEMBER.생성();
            RecruitMember member2 = PM_MEMBER.생성();
            RecruitMember member3 = DESIGNER_MEMBER.생성();
            List<RecruitMember> recruitMembers = List.of(member1, member2, member3);

            Recruitment recruitment = PROGRAMMING_POST.생성(USER, recruitTags, recruitMembers);
            recruitment.getPost().updateTitle("모집글 " + count);
            recruitmentRepository.save(recruitment);
        }

        em.flush();
        em.clear();
    }

    @Test
    void searchOne() {

    }

    @Test
    void searchAll() {
    }
}