package com.moa.base;

import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.member.AttendMemberRepository;
import com.moa.domain.member.RecruitMemberRepository;
import com.moa.domain.notice.NoticeRepository;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.user.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractServiceTest {

    @Mock
    protected UserRepository userRepository;
    @Mock
    protected ApplimentMemberRepository applimentMemberRepository;
    @Mock
    protected PasswordEncoder passwordEncoder;
    @Mock
    protected RecruitMemberRepository recruitMemberRepository;
    @Mock
    protected RecruitmentRepository recruitmentRepository;

    @Mock
    protected NoticeRepository noticeRepository;
    @Mock
    protected AttendMemberRepository attendMemberRepository;
}
