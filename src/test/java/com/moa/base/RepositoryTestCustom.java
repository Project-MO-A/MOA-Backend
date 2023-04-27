package com.moa.base;


import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.recruit.tag.TagRepository;
import com.moa.domain.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest
public abstract class RepositoryTestCustom {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RecruitmentRepository recruitmentRepository;
    @Autowired
    protected TagRepository tagRepository;
    @Autowired
    protected ApplimentMemberRepository applimentMemberRepository;
    @Autowired
    protected EntityManager em;
}
