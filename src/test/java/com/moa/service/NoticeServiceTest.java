package com.moa.service;

import com.moa.domain.member.AttendMember;
import com.moa.domain.member.AttendMemberRepository;
import com.moa.domain.notice.Notice;
import com.moa.domain.notice.NoticeRepository;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.member.RecruitMemberRequest;
import com.moa.dto.notice.NoticesResponse;
import com.moa.dto.notice.PostNoticeRequest;
import com.moa.dto.notice.UpdateNoticeRequest;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.user.UserEmailResponse;
import com.moa.dto.user.UserSignupRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class NoticeServiceTest {

    @Autowired
    NoticeService noticeService;

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    RecruitmentService recruitmentService;

    @Autowired
    TagService tagService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AttendMemberRepository attendMemberRepository;

    Long recruitmentId;

    @BeforeEach
    void init() {
        UserEmailResponse userEmailResponse = userService.saveUser(new UserSignupRequest("email", "password", "name", "nickname", "details", 34.23, 34.545, List.of("백엔드", "java")));
        String email = userEmailResponse.email();
        User user = userRepository.findByEmail(email).get();

        List<String> categoryString = List.of("프로젝트", "스터디");
        List<Tag> tag = tagService.update(categoryString);
        List<Long> categoryIds = tag.stream()
                .map(Tag::getId)
                .toList();

        List<RecruitMemberRequest> memberFields = List.of(
                new RecruitMemberRequest("backend", 2),
                new RecruitMemberRequest("frontend", 2)
        );

        recruitmentId = recruitmentService.post(user.getId(),
                new RecruitPostRequest("title",
                        "content",
                        memberFields,
                        categoryString), tag);
    }

    @Test
    @DisplayName("post Notice success")
    void postNoticeSuccess() {
        //given
        Long noticeId = noticeService.post(recruitmentId, new PostNoticeRequest(
                "notice Title",
                "notice content",
                LocalDateTime.parse("2023-03-27T19:00:00"),
                "서울역",
                true
        ));

        //when
        List<AttendMember> attendMembers = attendMemberRepository.findAllByNoticeIdIn(List.of(noticeId));

        Notice newNotice = noticeRepository.findById(noticeId).get();

        //then
        assertThat(newNotice.getPost().getTitle()).isEqualTo("notice Title");
        assertThat(newNotice.getPost().getContent()).isEqualTo("notice content");
        assertThat(newNotice.getConfirmedTime()).isEqualTo(LocalDateTime.parse("2023-03-27 19:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertThat(newNotice.getConfirmedLocation()).isEqualTo("서울역");
        assertThat(newNotice.isCheckVote()).isEqualTo(true);
        assertThat(attendMembers.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("notice update success")
    void noticeUpdate() {
        //given
        Long noticeId = noticeService.post(recruitmentId, new PostNoticeRequest(
                "notice Title",
                "notice content",
                LocalDateTime.parse("2023-03-27T19:00:00"),
                "서울역",
                true
        ));

        LocalDateTime dateTime = LocalDateTime.of(2023, 3, 30, 12, 0);
        UpdateNoticeRequest request = new UpdateNoticeRequest("new Title", "", dateTime, null, null);

        //when
        Long updateNoticeId = noticeService.update(recruitmentId, noticeId, request);
        Notice newNotice = noticeRepository.findById(updateNoticeId).get();

        //then
        assertThat(newNotice.getPost().getTitle()).isEqualTo("new Title");
        assertThat(newNotice.getPost().getContent()).isEqualTo("notice content");
        assertThat(newNotice.getConfirmedTime()).isEqualTo(LocalDateTime.parse("2023-03-30 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertThat(newNotice.getConfirmedLocation()).isEqualTo("서울역");
        assertThat(newNotice.isCheckVote()).isEqualTo(true);
    }

    @Test
    @DisplayName("notice update fail - has no notice id")
    void noticeUpdateFail() {
        //given
        LocalDateTime dateTime = LocalDateTime.of(2023, 3, 30, 12, 0);
        UpdateNoticeRequest request = new UpdateNoticeRequest("new Title", "", dateTime, null, null);

        //when & then
        assertThatThrownBy(() -> noticeService.update(recruitmentId, 0L, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("notice delete success")
    void noticeDeleteSuccess() {
        //given
        Long noticeId = noticeService.post(recruitmentId, new PostNoticeRequest(
                "notice Title",
                "notice content",
                LocalDateTime.parse("2023-03-27T19:00:00"),
                "서울역",
                true
        ));

        //when
        Long deletedNoticeId = noticeService.delete(recruitmentId, noticeId);

        Optional<Notice> deleteNotice = noticeRepository.findById(deletedNoticeId);
        //then
        assertThat(noticeId).isEqualTo(deletedNoticeId);
        assertThat(deleteNotice.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("notice delete fail - has no notice id")
    void noticeDeleteFail() {
        //when & then
        assertThatThrownBy(() -> noticeService.delete(recruitmentId, 0L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("find all notices")
    void findAllNotices() {
        //given
        List<Long> noticeIds = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Long noticeId = noticeService.post(recruitmentId, new PostNoticeRequest(
                    "notice Title" + i,
                    "notice content" + i,
                    LocalDateTime.parse("2023-03-27T19:00:00"),
                    "서울역",
                    true
            ));

            noticeIds.add(noticeId);
        }

        //when
        NoticesResponse response = noticeService.findAll(recruitmentId);

        //then
        assertThat(response.notices().size()).isEqualTo(5);
        assertThat(response.notices().get(noticeIds.get(0)).title()).isEqualTo("notice Title1");
    }
}