package com.moa.domain.member;

import com.moa.domain.notice.Post;
import com.moa.domain.recruit.Category;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
class RecruitMemberRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private RecruitMemberRepository recruitMemberRepository;

    private Recruitment recruitment1;
    private Recruitment recruitment2;

    @BeforeEach
    void init() {
        User user = userRepository.save(User.builder().name("admin").build());
        recruitment1 = recruitmentRepository.save(new Recruitment(user, new Post("recruitment1", "content"),
                RecruitStatus.RECRUITING, Category.EMPLOYMENT));
        recruitment2 = recruitmentRepository.save(new Recruitment(user, new Post("recruitment2", "content"),
                RecruitStatus.RECRUITING, Category.EMPLOYMENT));
        recruitMemberRepository.save(new RecruitMember(recruitment1, "백엔드", 5));
        recruitMemberRepository.save(new RecruitMember(recruitment1, "프론트엔드", 2));

        recruitMemberRepository.save(new RecruitMember(recruitment2, "백엔드", 3));
        recruitMemberRepository.save(new RecruitMember(recruitment2, "프론트엔드", 3));
        recruitMemberRepository.save(new RecruitMember(recruitment2, "디자이너", 2));
        recruitMemberRepository.save(new RecruitMember(recruitment2, "매니저", 2));
    }

    @Test
    @DisplayName("특정 모집글에 있는 특정 모집분야관련 정보를 가져온다")
    void findByRecruitField() {
        //given
        Long recruitmentId = recruitment1.getId();
        String field = "백엔드";

        //when
        Optional<RecruitMember> recruitMember = recruitMemberRepository.findByRecruitFieldAndRecruitmentId(field, recruitmentId);

        //then
        assertThat(recruitMember.get().getRecruitField()).isEqualTo(field);
        assertThat(recruitMember.get().getTotalRecruitCount()).isEqualTo(5);
        assertThat(recruitMember.get().getRecruitment().getId()).isEqualTo(recruitment1.getId());
    }
}