package com.moa.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruitMemberRepository extends JpaRepository<RecruitMember, Long> {
    Optional<RecruitMember> findByRecruitFieldAndRecruitmentId(String recruitField, Long recruitmentId);
}
