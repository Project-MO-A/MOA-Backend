package com.moa.service;

import com.moa.domain.recruit.RecruitState;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.user.UserRepository;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.user.UserSignupRequest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.moa.dto.recruit.RecruitPostRequest.RecruitMemberRequest;
import static com.moa.dto.recruit.RecruitPostRequest.builder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RecruitServiceTest {
    @Autowired
    RecruitService recruitService;
    @Autowired
    RecruitmentRepository recruitmentRepository;

    @Autowired
    UserService userService;
    @Autowired
    EntityManager em;

    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryService categoryService;

    private static final String TEST_EMAIL = "test2@naver.com";

    @BeforeEach
    void setUser() {
        List<String> interests = new ArrayList<>();
        interests.add("Java");
        interests.add("Python");

        UserSignupRequest testUser = UserSignupRequest.builder()
                .email(TEST_EMAIL)
                .password("qwer1234")
                .name("기우")
                .nickname("john")
                .details("Hello")
                .locationLatitude(23.1551134)
                .locationLongitude(51.2341355)
                .interests(interests)
                .build();

        userService.saveUser(testUser);
    }

    @DisplayName("post - 모집글 등록에 성공한다.")
    @Test
    void post() {
        Long userId = userRepository.findByEmail(TEST_EMAIL).orElseThrow().getId();
        RecruitMemberRequest memberRequest2 = new RecruitMemberRequest("백엔드", 5);
        RecruitMemberRequest memberRequest1 = new RecruitMemberRequest("프론트엔드", 5);

        List<RecruitMemberRequest> list = List.of(memberRequest1, memberRequest2);
        List<String> category = List.of("프로젝트", "개발", "팀프로젝트");
        RecruitPostRequest request = builder()
                .title("모집글 1")
                .content("네이스")
                .memberFields(list)
                .category(category)
                .build();

        categoryService.save(category);
        List<Long> categoryId = categoryService.getId(category);
        Long postId = recruitService.post(userId, request, categoryId);

        em.flush();
        em.clear();
        Recruitment recruitment = recruitmentRepository.findById(postId).get();

        assertThat(recruitment.getUser().getName()).isEqualTo("기우");
        assertThat(recruitment.getState()).isEqualTo(RecruitState.RECRUITING);
        assertThat(recruitment.getCategory().size()).isEqualTo(3);
        assertThat(recruitment.getMembers().size()).isEqualTo(2);
    }
}