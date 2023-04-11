package com.moa.service;

import com.moa.base.AbstractServiceTest;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.reply.Reply;
import com.moa.domain.user.User;
import com.moa.dto.reply.RepliesInfo;
import com.moa.dto.reply.ReplyPostRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ReplyServiceTest extends AbstractServiceTest {

    @InjectMocks
    private ReplyService replyService;

    @Test
    @DisplayName("댓글 등록에 성공한다")
    void successPostReply() throws NoSuchFieldException, IllegalAccessException {
        //given
        ReplyPostRequest request = new ReplyPostRequest("안녕하세요", 1L, 1L, null);
        User user = User.builder().build();
        Recruitment recruitment = Recruitment.builder().build();
        given(userRepository.getReferenceById(1L)).willReturn(user);
        given(recruitmentRepository.getReferenceById(1L)).willReturn(recruitment);

        //reflection
        Reply reply = request.toEntity(user, recruitment);
        Field id = reply.getClass().getDeclaredField("id");
        id.setAccessible(true);
        id.set(reply, 1L);

        given(replyRepository.save(any(Reply.class))).willReturn(reply);

        //when
        Long replyId = replyService.createReply(request);

        //then
        assertAll(
                () -> assertThat(replyId).isEqualTo(1L),
                () -> verify(userRepository).getReferenceById(1L),
                () -> verify(recruitmentRepository).getReferenceById(1L),
                () -> verify(replyRepository).save(any(Reply.class))
        );
    }

    @Test
    @DisplayName("상위 댓글이 없어 댓글 등록에 실패한다")
    void failPostReply() {
        //given
        ReplyPostRequest request = new ReplyPostRequest("안녕하세요", 1L, 1L, 100L);
        given(replyRepository.findById(request.parentId())).willThrow(EntityNotFoundException.class);

        //when && then
        assertAll(
                () -> assertThatThrownBy(() -> replyService.createReply(request))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(userRepository, never()).getReferenceById(1L),
                () -> verify(recruitmentRepository, never()).getReferenceById(1L),
                () -> verify(replyRepository, never()).save(any(Reply.class)),
                () -> verify(replyRepository).findById(100L)
        );
    }

    @Test
    @DisplayName("댓글 목록을 가져온다")
    void getReplyList() throws NoSuchFieldException, IllegalAccessException {
        //given
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        Recruitment recruitment = Recruitment.builder().build();
        Reply reply1 = new Reply("content1", null, user1, recruitment);
        Reply reply2 = new Reply("content2", null, user2, recruitment);
        Reply reply3 = new Reply("content3", 1L, user1, recruitment);
        Reply reply4 = new Reply("content4", 1L, user2, recruitment);
        Reply reply5 = new Reply("content5", 2L, user1, recruitment);
        List<Reply> replies = List.of(reply1, reply2, reply3, reply4, reply5);

        for (int i = 0; i < replies.size(); i++) {
            Reply reply = replies.get(i);
            Field id = reply.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(reply, (long) i);
            Field createdDate = reply.getClass().getSuperclass().getDeclaredField("createdDate");
            createdDate.setAccessible(true);
            createdDate.set(reply, LocalDateTime.now());
        }

        given(replyRepository.findByRecruitmentIdOrderByParentIdAsc(1L)).willReturn(replies);

        //when
        RepliesInfo repliesInfo = replyService.getAllReply(1L);

        //then
        assertThat(repliesInfo.getInfo().size()).isEqualTo(2);
    }

}