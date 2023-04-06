package com.moa.service.unit;

import com.moa.base.AbstractServiceTest;
import com.moa.domain.member.AttendMember;
import com.moa.domain.member.Attendance;
import com.moa.domain.notice.Notice;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import com.moa.dto.notice.NoticesResponse;
import com.moa.dto.notice.NoticesResponse.NoticeResponse;
import com.moa.dto.notice.PostNoticeRequest;
import com.moa.dto.notice.UpdateNoticeRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class NoticeServiceUnitTest extends AbstractServiceTest {

    @InjectMocks
    private NoticeService noticeService;

    private final Long noticeId = 1L;
    private final Long recruitmentId = 1L;
    private final Recruitment recruitment = Recruitment.builder().build();
    private final Notice notice = new ProxyNotice(recruitment, new Post("title", "content"),
            LocalDateTime.parse("2023-03-30T18:00:00"), "서울역", true, 1L);

    @Test
    @DisplayName("공지글 등록에 성공한다")
    void successPostNotice() {
        //given
        PostNoticeRequest request = new PostNoticeRequest("content", true);
        Notice savedNotice = request.toEntity(recruitment);

        given(recruitmentRepository.getReferenceById(recruitmentId)).willReturn(recruitment);
        given(noticeRepository.save(any(Notice.class))).willReturn(savedNotice);
        doNothing().when(attendMemberRepository).saveFromApplimentMember(savedNotice, recruitmentId);

        //when
        noticeService.post(recruitmentId, request);

        //then
        assertAll(
                () -> verify(recruitmentRepository).getReferenceById(recruitmentId),
                () -> verify(noticeRepository).save(any(Notice.class)),
                () -> verify(attendMemberRepository).saveFromApplimentMember(savedNotice, recruitmentId)
        );
    }

    @Test
    @DisplayName("공지사항 수정에 성공한다")
    void successUpdateNotice() {
        //given
        UpdateNoticeRequest request = new UpdateNoticeRequest("content2", null);

        given(recruitmentRepository.getReferenceById(recruitmentId)).willReturn(recruitment);
        given(noticeRepository.findById(noticeId)).willReturn(Optional.of(notice));

        //when
        noticeService.update(recruitmentId, noticeId, request);

        //then
        assertAll(
                () -> assertThat(notice.getPost().getContent()).isEqualTo(notice.getPost().getContent()),
                () -> assertThat(notice.isCheckVote()).isEqualTo(notice.isCheckVote()),
                () -> verify(recruitmentRepository).getReferenceById(recruitmentId),
                () -> verify(noticeRepository).findById(noticeId)
        );
    }

    @Test
    @DisplayName("공지사항이 없어 수정에 실패한다")
    void failUpdateNoticeByNoNotice() {
        //given
        UpdateNoticeRequest request = new UpdateNoticeRequest( null, null);

        given(recruitmentRepository.getReferenceById(recruitmentId)).willReturn(recruitment);
        given(noticeRepository.findById(noticeId)).willThrow(EntityNotFoundException.class);

        //when & then
        assertAll(
                () -> assertThatThrownBy(() -> noticeService.update(recruitmentId, noticeId, request))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(recruitmentRepository).getReferenceById(recruitmentId),
                () -> verify(noticeRepository).findById(noticeId)
        );
    }

    @Test
    @DisplayName("공지사항 삭제에 성공한다")
    void successDeleteNotice() {
        //given
        given(noticeRepository.findByIdAndRecruitmentId(noticeId, recruitmentId)).willReturn(Optional.of(notice));

        //when
        Long deletedNoticeId = noticeService.delete(recruitmentId, noticeId);

        //then
        assertAll(
                () -> assertThat(deletedNoticeId).isEqualTo(noticeId),
                () -> verify(noticeRepository).findByIdAndRecruitmentId(noticeId, recruitmentId),
                () -> verify(noticeRepository).delete(notice)
        );
    }

    @Test
    @DisplayName("공지사항이 없어 삭제에 실패한다")
    void failDeleteNoticeByNoNotice() {
        //given
        given(noticeRepository.findByIdAndRecruitmentId(noticeId, recruitmentId)).willThrow(EntityNotFoundException.class);

        //when & then
        assertAll(
                () -> assertThatThrownBy(() -> noticeService.delete(recruitmentId, noticeId))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(noticeRepository).findByIdAndRecruitmentId(noticeId, recruitmentId),
                () -> verify(noticeRepository, never()).delete(notice)
        );
    }

    @Test
    @DisplayName("특정 모집글에서 작성된 모든 공지사항을 불러온다")
    void findAllNotice() {
        //given
        Notice notice1 = new ProxyNotice(recruitment, new Post("title1", "content1"), LocalDateTime.parse("2023-03-30T18:00:00"), "서울역", false, 1L);
        Notice notice2 = new ProxyNotice(recruitment, new Post("title2", "content2"), LocalDateTime.parse("2023-04-03T20:00:00"), "용산역", true, 2L);
        Notice notice3 = new ProxyNotice(recruitment, new Post("title3", "content3"), LocalDateTime.parse("2023-03-05T18:00:00"), "사당역", true, 3L);
        given(noticeRepository.findAllByRecruitmentId(recruitmentId)).willReturn(List.of(notice1, notice2, notice3));
        given(attendMemberRepository.findAllByNoticeIdIn(List.of(1L, 2L, 3L))).willReturn(List.of(new AttendMember(Attendance.ATTENDANCE, User.builder().name("user1").build(), notice1), new AttendMember(Attendance.ATTENDANCE, User.builder().name("user2").build(), notice1), new AttendMember(Attendance.ATTENDANCE, User.builder().name("user1").build(), notice2), new AttendMember(Attendance.ATTENDANCE, User.builder().name("user2").build(), notice2), new AttendMember(Attendance.ATTENDANCE, User.builder().name("user3").build(), notice2), new AttendMember(Attendance.ATTENDANCE, User.builder().name("user3").build(), notice3)));

        //when
        NoticesResponse response = noticeService.findAll(recruitmentId);

        //then
        assertAll(() -> assertThat(response.notices().size()).isEqualTo(3), () -> {
            Long id = 1L;
            for (Map.Entry<Long, NoticeResponse> entry : response.notices().entrySet()) {
                assertThat(entry.getKey()).isEqualTo(id);
                assertThat(entry.getValue().content()).isEqualTo("content" + id++);
            }
        });
    }

    private static class ProxyNotice extends Notice {
        private final Long id;

        public ProxyNotice(Recruitment recruitment, Post post, LocalDateTime confirmedTime, String confirmedLocation, boolean checkVote, Long id) {
            super(recruitment, post, confirmedTime, confirmedLocation, checkVote);
            this.id = id;
        }

        @Override
        public Long getId() {
            return this.id;
        }

        @Override
        public LocalDateTime getCreatedDate() {
            return LocalDateTime.now();
        }
    }
}