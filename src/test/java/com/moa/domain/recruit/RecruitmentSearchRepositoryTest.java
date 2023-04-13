package com.moa.domain.recruit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.moa.base.RepositoryTestCustom;
import com.moa.domain.base.SearchParam;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.User;
import com.moa.dto.recruit.RecruitmentInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.moa.domain.base.SearchParam.*;
import static com.moa.domain.recruit.Category.HOBBY;
import static com.moa.domain.recruit.RecruitStatus.RECRUITING;
import static com.moa.support.fixture.RecruitMemberFixture.*;
import static com.moa.support.fixture.RecruitmentFixture.*;
import static com.moa.support.fixture.TagFixture.*;
import static com.moa.support.fixture.UserFixture.KAI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

class RecruitmentSearchRepositoryTest extends RepositoryTestCustom {
    @Autowired
    private RecruitmentSearchRepository searchRepository;

    @BeforeEach
    void setUp() {
        // 유저
        User USER = KAI.생성();
        userRepository.saveAll(List.of(USER));

        // 태그
        List<Tag> programmingTags = BACKEND_TAG.생성();
        List<Tag> toeicTags = TOEIC_TAG.생성();
        List<Tag> bookTags = BOOK_TAG.생성();

        tagRepository.saveAll(programmingTags);
        tagRepository.saveAll(toeicTags);
        tagRepository.saveAll(bookTags);

        for (int count = 1; count <= 10; count++) {
            List<RecruitTag> recruitTags = programmingTags.stream().map(RecruitTag::new).toList();
            List<RecruitMember> recruitMembers = List.of(BACKEND_MEMBER.생성(), FRONTEND_MEMBER.생성(), DESIGNER_MEMBER.생성());

            Recruitment recruitment = PROGRAMMING_POST.생성(USER, recruitTags, recruitMembers);
            recruitment.getPost().updateTitle("프로젝트 인원 모집 " + count);
            if (count % 2 == 0) recruitment.updateState(2);

            recruitmentRepository.save(recruitment);
        }

        for (int count = 1; count <= 10; count++) {
            List<RecruitTag> recruitTags = toeicTags.stream().map(RecruitTag::new).toList();
            List<RecruitMember> recruitMembers = List.of(ALL.생성());

            Recruitment recruitment = LANGUAGE_POST.생성(USER, recruitTags, recruitMembers);
            recruitment.getPost().updateTitle("토익 스터디 인원 모집 " + count);
            if (count % 2 == 0) recruitment.updateState(2);

            recruitmentRepository.save(recruitment);
        }

        for (int count = 1; count <= 10; count++) {
            List<RecruitTag> recruitTags = bookTags.stream().map(RecruitTag::new).toList();
            List<RecruitMember> recruitMembers = List.of(ALL.생성());

            Recruitment recruitment = HOBBY_POST.생성(USER, recruitTags, recruitMembers);
            recruitment.getPost().updateTitle("독서 스터디 인원 모집 " + count);
            if (count % 2 == 0) recruitment.updateState(2);

            recruitmentRepository.save(recruitment);
        }

        em.flush();
        em.clear();
    }

    @DisplayName("하나의 모집글을 검색한다. (제목)")
    @Test
    void searchOne() {
        //given
        final String TITLE = "독서 스터디 인원 모집 1";
        Map<String, String> searchCondition = new ConcurrentHashMap<>();
        searchCondition.put(SearchParam.TITLE.getParamKey(), TITLE);

        //when
        RecruitmentInfo recruitmentInfo = searchRepository.searchOne(searchCondition);
        System.out.println(recruitmentInfo);

        //then
        assertAll(
                () -> assertThat(recruitmentInfo.getTitle()).isEqualTo(TITLE),
                () -> assertThat(recruitmentInfo.getTags().size()).isEqualTo(4),
                () -> assertThat(recruitmentInfo.getRecruitStatus()).isEqualTo(RECRUITING.getStatus()),
                () -> assertThat(recruitmentInfo.getTotalCount()).isEqualTo(7),
                () -> assertThat(recruitmentInfo.getCategory()).isEqualTo(HOBBY.getName())
        );
    }

    @DisplayName("여러 모집글을 검색한다. (제목)")
    @Test
    void searchAll_Title() {
        //given
        final String TITLE_PREFIX = "토익";
        Map<String, String> searchCondition = new ConcurrentHashMap<>();
        searchCondition.put(SearchParam.TITLE.getParamKey(), TITLE_PREFIX);

        //when
        PageRequest pageRequest = PageRequest.of(0, 100, DESC, CREATED_DATE.getParamKey());
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(searchCondition, pageRequest);

        for (RecruitmentInfo recruitmentInfo : recruitmentInfoPage) {
            System.out.println(recruitmentInfo);
        }

        //then
        assertAll(
                () -> assertThat(recruitmentInfoPage.getContent().size()).isEqualTo(10),
                () -> assertThat(recruitmentInfoPage.getTotalElements()).isEqualTo(10),
                () -> assertThat(recruitmentInfoPage.getContent().stream()
                        .filter(info -> info.getTitle().startsWith("토익"))
                        .count())
                        .isEqualTo(10)
        );
    }

    @DisplayName("여러 모집글을 검색한다. (카테고리)")
    @Test
    void searchAll_Category() {
        //given
        final String CATEGORY_NAME = HOBBY.getName();
        Map<String, String> searchCondition = new ConcurrentHashMap<>();
        searchCondition.put(CATEGORY.getParamKey(), CATEGORY_NAME);

        //when
        PageRequest pageRequest = PageRequest.of(0, 100, DESC, CREATED_DATE.getParamKey());
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(searchCondition, pageRequest);

        for (RecruitmentInfo recruitmentInfo : recruitmentInfoPage) {
            System.out.println(recruitmentInfo);
        }

        //then
        assertAll(
                () -> assertThat(recruitmentInfoPage.getContent().size()).isEqualTo(10),
                () -> assertThat(recruitmentInfoPage.getTotalElements()).isEqualTo(10),
                () -> assertThat(recruitmentInfoPage.getContent().stream()
                        .filter(info -> info.getTitle().startsWith("독서"))
                        .count())
                        .isEqualTo(10)
        );
    }

    @DisplayName("여러 모집글을 검색한다. (태그)")
    @Test
    void searchAll_Tag() {
        //given
        final String TAG_NAME = BACKEND_TAG.getTags().get(2);
        Map<String, String> searchCondition = new ConcurrentHashMap<>();
        searchCondition.put(TAG.getParamKey(), TAG_NAME);

        //when
        PageRequest pageRequest = PageRequest.of(0, 100, DESC, CREATED_DATE.getParamKey());
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(searchCondition, pageRequest);

        for (RecruitmentInfo recruitmentInfo : recruitmentInfoPage) {
            System.out.println(recruitmentInfo);
        }

        //then
        assertAll(
                () -> assertThat(recruitmentInfoPage.getContent().size()).isEqualTo(10),
                () -> assertThat(recruitmentInfoPage.getTotalElements()).isEqualTo(10),
                () -> assertThat(recruitmentInfoPage.getContent().stream()
                        .filter(info -> info.getTitle().startsWith("프로젝트"))
                        .count())
                        .isEqualTo(10)
        );
    }

    @DisplayName("여러 모집글을 검색한다. (모집 상태)")
    @Test
    void searchAll_State() {
        //given
        final int CODE = RECRUITING.getCode();
        Map<String, String> searchCondition = new ConcurrentHashMap<>();
        searchCondition.put(STATE_CODE.getParamKey(), String.valueOf(CODE));

        //when
        PageRequest pageRequest = PageRequest.of(0, 100, DESC, CREATED_DATE.getParamKey());
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(searchCondition, pageRequest);

        for (RecruitmentInfo recruitmentInfo : recruitmentInfoPage) {
            System.out.println(recruitmentInfo);
        }

        //then
        assertAll(
                () -> assertThat(recruitmentInfoPage.getContent().size()).isEqualTo(15),
                () -> assertThat(recruitmentInfoPage.getTotalElements()).isEqualTo(15),
                () -> assertThat(recruitmentInfoPage.getContent().stream()
                        .filter(info -> info.getRecruitStatus()
                                .equals(RECRUITING.getStatus()))
                        .count())
                        .isEqualTo(15)
        );
    }

    @DisplayName("여러 모집글을 검색한다. (n일전 게시글 조회)")
    @Test
    void searchAll_WithInDays() {
        //given
        final String DAYS_AGO = "3";
        Map<String, String> searchCondition = new ConcurrentHashMap<>();
        searchCondition.put(SearchParam.DAYS_AGO.getParamKey(), DAYS_AGO);

        //when
        PageRequest pageRequest = PageRequest.of(0, 100, DESC, CREATED_DATE.getParamKey());
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(searchCondition, pageRequest);

        for (RecruitmentInfo recruitmentInfo : recruitmentInfoPage) {
            System.out.println(recruitmentInfo);
        }

        LocalDateTime startTime = LocalDateTime.now().minusDays(Long.parseLong(DAYS_AGO));

        //then
        assertAll(
                () -> assertThat(recruitmentInfoPage.getContent().size()).isEqualTo(30),
                () -> assertThat(recruitmentInfoPage.getTotalElements()).isEqualTo(30),
                () -> assertThat(recruitmentInfoPage.getContent().stream()
                        .filter(info -> info.getCreatedDate().isAfter(startTime))
                        .count())
                        .isEqualTo(30)
        );
    }

    @DisplayName("여러 모집글을 검색한다. (상태, 태그)")
    @Test
    void searchAll_StateTag() {
        //given
        final String TAG_NAME = "백엔드";
        Map<String, String> searchCondition = new ConcurrentHashMap<>();
        searchCondition.put(TAG.getParamKey(), TAG_NAME);
        searchCondition.put(STATE_CODE.getParamKey(), String.valueOf(RECRUITING.getCode()));

        //when
        PageRequest pageRequest = PageRequest.of(0, 100, DESC, CREATED_DATE.getParamKey());
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(searchCondition, pageRequest);

        for (RecruitmentInfo recruitmentInfo : recruitmentInfoPage) {
            System.out.println(recruitmentInfo);
        }

        //then
        assertAll(
                () -> assertThat(recruitmentInfoPage.getContent().size()).isEqualTo(5),
                () -> assertThat(recruitmentInfoPage.getTotalElements()).isEqualTo(5),
                () -> assertThat(recruitmentInfoPage.getContent().stream()
                        .filter(info -> info.getTitle().startsWith("프로젝트") &&
                                info.getRecruitStatus().equals(RECRUITING.getStatus()))
                        .count())
                        .isEqualTo(5)
        );
    }

    @DisplayName("여러 모집글을 검색한다. (카테고리, 제목)")
    @Test
    void searchAll_CategoryTitle() {
        //given
        Map<String, String> searchCondition = new ConcurrentHashMap<>();
        searchCondition.put(CATEGORY.getParamKey(), "프로그래밍");
        searchCondition.put(TITLE.getParamKey(), "프로젝트");

        //when
        PageRequest pageRequest = PageRequest.of(0, 100, DESC, CREATED_DATE.getParamKey());
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(searchCondition, pageRequest);

        for (RecruitmentInfo recruitmentInfo : recruitmentInfoPage) {
            System.out.println(recruitmentInfo);
        }

        //then
        assertAll(
                () -> assertThat(recruitmentInfoPage.getContent().size()).isEqualTo(10),
                () -> assertThat(recruitmentInfoPage.getTotalElements()).isEqualTo(10),
                () -> assertThat(recruitmentInfoPage.getContent().stream()
                        .filter(info -> info.getTitle().startsWith("프로젝트") &&
                                info.getCategory().equals("프로그래밍"))
                        .count())
                        .isEqualTo(10)
        );
    }

    @DisplayName("특정 페이지의 데이터가 조회된다.")
    @Test
    void searchAll_Paging() throws JsonProcessingException {
        //given
        PageRequest pageRequest = PageRequest.of(2, 5, DESC, CREATED_DATE.getParamKey());

        //when
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(new ConcurrentHashMap<>(), pageRequest);

        //then
        assertAll(
                () -> assertThat(recruitmentInfoPage.getContent().size()).isEqualTo(5),
                () -> assertThat(recruitmentInfoPage.getTotalElements()).isEqualTo(30),
                () -> assertThat(recruitmentInfoPage.getTotalPages()).isEqualTo(6),
                () -> assertThat(recruitmentInfoPage.getSize()).isEqualTo(5)
        );
    }

    @DisplayName("다음 페이지 존재 여부를 포함한 특정 페이지의 데이터가 조회된다.")
    @Test
    void searchSlice() throws JsonProcessingException {
        //given
        PageRequest pageRequest = PageRequest.of(1, 5, DESC, CREATED_DATE.getParamKey());
        Map<String, String> searchCondition = new ConcurrentHashMap<>();
        searchCondition.put(TITLE.getParamKey(), "프로젝트");

        //when
        Slice<RecruitmentInfo> recruitmentInfos = searchRepository.searchSlice(searchCondition, pageRequest);

        //then
        assertAll(
                () -> assertThat(recruitmentInfos.getContent().get(0).getTitle()).contains("5"),
                () -> assertThat(recruitmentInfos.getNumberOfElements()).isEqualTo(5),
                () -> assertThat(recruitmentInfos.isLast()).isTrue(),
                () -> assertThat(recruitmentInfos.isFirst()).isFalse()
        );
    }

    @DisplayName("모집글이 최신순으로 정렬된다.")
    @Test
    void searchAll_OrderByCreatedDateDesc() {
        //given
        PageRequest pageRequest = PageRequest.of(2, 5, DESC, CREATED_DATE.getParamKey());

        //when
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(new ConcurrentHashMap<>(), pageRequest);

        //then
        List<RecruitmentInfo> content = recruitmentInfoPage.getContent();
        assertAll(
                () -> assertThat(content.get(0).getCreatedDate()
                        .isAfter(content.get(1).getCreatedDate())).isTrue(),
                () -> assertThat(recruitmentInfoPage.getSort()
                        .getOrderFor(CREATED_DATE.getParamKey()).isDescending()).isTrue()
        );
    }

    @DisplayName("모집글이 오래된순으로 정렬된다.")
    @Test
    void searchAll_OrderByCreatedDateAsc() {
        //given
        PageRequest pageRequest = PageRequest.of(2, 5, ASC, CREATED_DATE.getParamKey());

        //when
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(new ConcurrentHashMap<>(), pageRequest);

        //then
        List<RecruitmentInfo> content = recruitmentInfoPage.getContent();
        assertAll(
                () -> assertThat(content.get(0).getCreatedDate()
                        .isBefore(content.get(1).getCreatedDate())).isTrue(),
                () -> assertThat(recruitmentInfoPage.getSort()
                        .getOrderFor(CREATED_DATE.getParamKey()).isAscending()).isTrue()
        );
    }

    @DisplayName("모집글이 최신순으로 페이징 되어 반환된다")
    @Test
    void searchAll_OrderByPaging() throws JsonProcessingException {
        //given
        PageRequest pageRequest = PageRequest.of(2, 5, DESC, CREATED_DATE.getParamKey());

        //when
        Page<RecruitmentInfo> recruitmentInfoPage = searchRepository.searchPage(new ConcurrentHashMap<>(), pageRequest);

        //then
        List<RecruitmentInfo> content = recruitmentInfoPage.getContent();
        assertAll(
                () -> assertThat(content.get(0).getCreatedDate()
                        .isAfter(content.get(1).getCreatedDate())).isTrue(),
                () -> assertThat(recruitmentInfoPage.getSort()
                        .getOrderFor(CREATED_DATE.getParamKey()).isDescending()).isTrue(),
                () -> assertThat(recruitmentInfoPage.getTotalElements()).isEqualTo(30),
                () -> assertThat(recruitmentInfoPage.getSize()).isEqualTo(5)
        );
    }

}