package com.moa.domain.notice;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@AutoConfigureTestDatabase(replace = NONE)
@DataJpaTest(showSql = false)
class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private UserRepository userRepository;

    private Recruitment recruitment;

    private Notice saved1;
    private Notice saved2;

    @BeforeEach
    void init() {
        User user = userRepository.save(User.builder().name("admin").build());
        recruitment = recruitmentRepository.save(new Recruitment(user, new Post("title", "content"),
                RecruitStatus.RECRUITING, Category.EMPLOYMENT));
        saved1 = noticeRepository.save(new Notice(recruitment, new Post("notice1", "first notice"),
                LocalDateTime.parse("2023-04-01T12:00:00"), "서울역", 0.0, 0.0,true));
        saved2 = noticeRepository.save(new Notice(recruitment, new Post("notice2", "second notice"),
                LocalDateTime.parse("2023-04-02T12:00:00"), "용산역", 0.0, 0.0,true));
    }

    @Test
    @DisplayName("특정 모집글에 작성된 공지사항 모두를 가져온다")
    void findAllByRecruitmentId() {
        //given
        Long recruitmentId = recruitment.getId();

        //when
        List<Notice> notices = noticeRepository.findAllByRecruitmentId(recruitmentId);

        //then
        assertThat(notices.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("특정 모집글에 작성된 특정 공지사항을 가져온다")
    void findByIdAndRecruitmentId() {
        //given
        Long recruitmentId = recruitment.getId();

        //when
        Optional<Notice> notice = noticeRepository.findByIdAndRecruitmentId(saved1.getId(), recruitmentId);

        //then
        assertThat(notice.isPresent()).isTrue();
        assertThat(notice.get().getRecruitment()).isEqualTo(recruitment);
        assertThat(notice.get().getPost().getTitle()).isEqualTo("notice1");
    }
}