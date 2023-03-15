package com.moa.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitMemberRepository extends JpaRepository<RecruitMember, Long> {
    RecruitMember findByRecruitFieldAndRecruitmentId(String recruitField, Long recruitmentId);
}
