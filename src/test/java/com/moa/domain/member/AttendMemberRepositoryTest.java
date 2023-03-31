package com.moa.domain.member;

import com.moa.domain.notice.Notice;
import com.moa.domain.notice.NoticeRepository;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.moa.domain.member.Attendance.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest(showSql = false)
class AttendMemberRepositoryTest {

    @Autowired
    private AttendMemberRepository attendMemberRepository;

    @Autowired
    private ApplimentMemberRepository applimentMemberRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private UserRepository userRepository;

    private Notice notice1;
    private Notice notice2;
    private Notice notice3;
    private User user;
    private Recruitment recruitment;

    @BeforeEach
    void init() {
        //admin 등록
        user = userRepository.save(User.builder().name("admin").build());
        recruitment = recruitmentRepository.save(Recruitment.builder().user(user).build());
        notice1 = noticeRepository.save(Notice.builder().recruitment(recruitment).build());
        notice2 = noticeRepository.save(Notice.builder().recruitment(recruitment).build());
        notice3 = noticeRepository.save(Notice.builder().recruitment(recruitment).post(new Post("notice3", "content")).build());
        attendMemberRepository.save(new AttendMember(NONE, user, notice1));
        attendMemberRepository.save(new AttendMember(NONE, user, notice3));

        //참여멤버 등록
        for (int i = 0; i < 5; i++) {
            User memberUser1 = userRepository.save(User.builder().name("user" + (i + 1)).build());
            User memberUser2 = userRepository.save(User.builder().name("user" + (i + 1)).build());
            applimentMemberRepository.save(new ApplimentMember(new RecruitMember(recruitment, "백엔드", 5),
                    memberUser1, ApprovalStatus.APPROVED));
            applimentMemberRepository.save(new ApplimentMember(new RecruitMember(recruitment, "백엔드", 5),
                    memberUser2, ApprovalStatus.APPROVED));
            attendMemberRepository.save(new AttendMember(NONE, memberUser1, notice1));
            attendMemberRepository.save(new AttendMember(NONE, memberUser2, notice2));
        }
    }

    @Test
    @DisplayName("모든 공지사항에 포함된 참석자 명단을 가져온다")
    void findAllByNotice() {
        //given
        List<Long> noticeIds = List.of(notice1.getId(), notice2.getId());

        //when
        List<AttendMember> attendMembers = attendMemberRepository.findAllByNoticeIdIn(noticeIds);

        //then
        assertAll(
                () -> assertThat(attendMembers.size()).isEqualTo(11),
                () -> assertThat(attendMembers.get(0).getNotice().getId()).isEqualTo(notice1.getId()),
                () -> assertThat(attendMembers.get(0).getUser().getName()).isEqualTo("admin")
        );
    }

    @Test
    @DisplayName("특정 공지사항에 참석하는 참석자 한명을 가져온다")
    void findOneMember() {
        //given
        Long noticeId = notice3.getId();
        Long userId = user.getId();

        //when
        AttendMember attendMember = attendMemberRepository.findByNoticeIdAndUserId(noticeId, userId).get();

        //then
        assertAll(
                () -> assertThat(attendMember.getAttendance()).isEqualTo(NONE),
                () -> assertThat(attendMember.getNotice().getPost().getTitle()).isEqualTo("notice3")
        );
    }

    @Test
    @DisplayName("공지사항 등록시 등록된 멤버들을 참여 멤버 테이블에 불러와 저장한다")
    void saveFromApplimentMember() {
        //when
        attendMemberRepository.saveFromApplimentMember(notice3, recruitment.getId());

        //then
        List<AttendMember> attendMember = attendMemberRepository.findAllByNoticeIdIn(List.of(notice3.getId()));
        assertThat(attendMember.size()).isEqualTo(11);
    }

}