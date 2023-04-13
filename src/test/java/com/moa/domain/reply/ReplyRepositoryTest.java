package com.moa.domain.reply;

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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@AutoConfigureTestDatabase(replace = NONE)
@DataJpaTest(showSql = false)
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private UserRepository userRepository;

    private Recruitment recruitment;
    private User user;

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder().name("admin").build());
        recruitment = recruitmentRepository.save(new Recruitment(user, new Post("title", "content"),
                RecruitStatus.RECRUITING, Category.EMPLOYMENT));
    }

    @Test
    @DisplayName("댓글 전체 목록을 가져온다 - by recruitmentId")
    void getAllReply() {
        //given
        Long recruitmentId = recruitment.getId();
        List<Reply> replies = List.of(
                new Reply("reply1", null, user, recruitment),
                new Reply("reply2", null, user, recruitment),
                new Reply("reply3", 1L, user, recruitment),
                new Reply("reply4", 1L, user, recruitment),
                new Reply("reply5", 2L, user, recruitment)
        );

        replyRepository.saveAll(replies);
        //when
        List<Reply> repliesResponse = replyRepository.findByRecruitmentIdOrderByParentIdAsc(recruitmentId);

        //then
        assertThat(repliesResponse.size()).isEqualTo(5);
        assertThat(repliesResponse.get(0).getParentId()).isNull();
        assertThat(repliesResponse.get(2).getParentId()).isEqualTo(1L);
    }
}