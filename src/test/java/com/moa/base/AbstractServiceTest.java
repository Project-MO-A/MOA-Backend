package com.moa.base;

import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.member.RecruitMemberRepository;
import com.moa.domain.user.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractServiceTest {

    @Mock
    protected UserRepository userRepository;
    @Mock
    protected ApplimentMemberRepository applimentMemberRepository;
    @Mock
    protected RecruitMemberRepository recruitMemberRepository;
}
