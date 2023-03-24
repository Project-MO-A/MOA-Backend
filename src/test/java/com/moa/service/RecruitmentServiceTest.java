package com.moa.service;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.user.UserRepository;
import com.moa.dto.member.RecruitMemberRequest;
import com.moa.dto.recruit.RecruitInfoResponse;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.recruit.RecruitUpdateRequest;
import com.moa.dto.user.UserSignupRequest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.moa.domain.recruit.RecruitStatus.COMPLETE;
import static com.moa.domain.recruit.RecruitStatus.RECRUITING;
import static com.moa.dto.recruit.RecruitPostRequest.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RecruitmentServiceTest {
    @Autowired
    RecruitmentService recruitmentService;
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
        //given
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
        categoryService.update(category);
        List<Long> categoryId = categoryService.getId(category);

        //when
        Long postId = recruitmentService.post(userId, request, categoryId);
        em.flush();
        em.clear();

        //then
        Recruitment recruitment = recruitmentRepository.findById(postId).orElseThrow();
        assertThat(recruitment.getUser().getName()).isEqualTo("기우");
        assertThat(recruitment.getStatus()).isEqualTo(RECRUITING);
        assertThat(recruitment.getCategory().size()).isEqualTo(3);
        assertThat(recruitment.getMembers().size()).isEqualTo(2);
        assertThat(recruitment.getMembers().get(0).getRecruitField()).containsAnyOf("백엔드", "프론트엔드");
    }

    @DisplayName("모집글 수정, 확인, 삭제 테스트")
    @Nested
    class Service {
        Long userId;
        Long recruitId;
        List<Long> categoryId;

        @BeforeEach
        void setUp() {
            //given
            userId = userRepository.findByEmail(TEST_EMAIL).orElseThrow().getId();
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
            categoryService.update(category);
            categoryId = categoryService.getId(category);
            recruitId = recruitmentService.post(userId, request, categoryId);
            em.flush();
            em.clear();
        }

        @DisplayName("getInfo - 모집글 정보를 불러오는데 성공한다.")
        @Test
        void getInfo() {
            //when
            RecruitInfoResponse info = recruitmentService.getInfo(recruitId);

            //then
            assertThat(info.getTitle()).isEqualTo("모집글 1");
            assertThat(info.getContent()).isEqualTo("네이스");
            assertThat(info.getState()).isEqualTo(RECRUITING);
            assertThat(info.getPostUser().userName()).isEqualTo("기우");
            assertThat(info.getCategories().size()).isEqualTo(3);
            assertThat(info.getMembers().size()).isEqualTo(2);
            assertThat(info.getMembers().get(0).recruitField()).containsAnyOf("프론트엔드");
        }

        @DisplayName("update - 모집글 수정에 성공한다. (타이틀, 내용)")
        @Test
        void updatePost() {
            //given
            RecruitUpdateRequest updateRequest = RecruitUpdateRequest.builder()
                    .title("web project")
                    .content("welcome")
                    .build();

            //when
            Long update = recruitmentService.update(recruitId, updateRequest,  new ArrayList<>());

            //then
            Recruitment updated = recruitmentRepository.findById(update).get();
            assertThat(updated.getPost().getTitle()).isEqualTo("web project");
            assertThat(updated.getPost().getContent()).isEqualTo("welcome");
            assertThat(updated.getMembers().size()).isEqualTo(2);
            assertThat(updated.getCategory().size()).isEqualTo(3);
        }

        @DisplayName("update - 모집글 수정에 성공한다. (모집 멤버 그룹)")
        @Test
        void updateRecruitMember() {
            //given
            RecruitMemberRequest memberUpdate1 = new RecruitMemberRequest("백엔드", 5);
            RecruitMemberRequest memberUpdate2 = new RecruitMemberRequest("프론트엔드", 5);
            RecruitMemberRequest memberUpdate3 = new RecruitMemberRequest("디자이너", 2);
            List<RecruitMemberRequest> updateMember = List.of(memberUpdate1, memberUpdate2, memberUpdate3);
            RecruitUpdateRequest updateRequest = RecruitUpdateRequest.builder()
                    .memberFields(updateMember)
                    .build();

            //when
            Long update = recruitmentService.update(recruitId, updateRequest, new ArrayList<>());

            //then
            Recruitment updated = recruitmentRepository.findById(update).get();
            List<String> fileds = updated.getMembers().stream()
                    .map(RecruitMember::getRecruitField)
                    .toList();

            assertThat(updated.getPost().getTitle()).isEqualTo("모집글 1");
            assertThat(updated.getPost().getContent()).isEqualTo("네이스");
            assertThat(updated.getCategory().size()).isEqualTo(3);
            assertThat(fileds.size()).isEqualTo(3);
            assertThat(fileds).containsOnly("백엔드", "프론트엔드", "디자이너");
        }

        @DisplayName("update - 모집글 수정에 성공한다. (카테고리)")
        @Test
        void updateCategory() {
            //given
            List<String> updateCategory = List.of("API", "백엔드", "크롤링", "자기개발");
            RecruitUpdateRequest updateRequest = RecruitUpdateRequest.builder()
                    .category(updateCategory)
                    .build();

            //when
            List<Long> updatedCategoryId = categoryService.updateAndReturnId(updateRequest.category()).get();
            Long update = recruitmentService.update(recruitId, updateRequest, updatedCategoryId);

            //then
            Recruitment updated = recruitmentRepository.findById(update).get();
            List<String> updatedCategory = updated.getCategory().stream()
                    .map(rc -> rc.getCategory().getName())
                    .toList();

            assertThat(updated.getPost().getTitle()).isEqualTo("모집글 1");
            assertThat(updated.getPost().getContent()).isEqualTo("네이스");
            assertThat(updated.getMembers().size()).isEqualTo(2);
            assertThat(updatedCategory.size()).isEqualTo(4);
            assertThat(updatedCategory).containsOnly("API", "백엔드", "크롤링", "자기개발");
        }

        @DisplayName("delete - 모집글 삭제에 성공한다.")
        @Test
        void delete() {
            //when
            Long delete = recruitmentService.delete(recruitId);

            //then
            assertThat(recruitmentRepository.findById(delete)).isEmpty();
        }

        @DisplayName("delete - 모집글 삭제에 실패한다. (잘못된 ID)")
        @Test
        void deleteFail() {
            //when
            assertThatThrownBy(() -> recruitmentService.delete(Long.MAX_VALUE))
                    .isInstanceOf(RuntimeException.class);
        }

        @DisplayName("updateStatus - 모집글 상태 변경에 성공한다.")
        @Test
        void updateState() {
            //when
            recruitmentService.updateStatus(recruitId, 2);

            //then
            Recruitment updated = recruitmentRepository.findById(recruitId).get();
            assertThat(updated.getStatus()).isEqualTo(COMPLETE);
        }
    }
}