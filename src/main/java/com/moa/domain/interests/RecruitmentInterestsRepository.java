package com.moa.domain.interests;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruitmentInterestsRepository extends JpaRepository<RecruitmentInterest, Long> {
    Optional<RecruitmentInterest> findByUserIdAndRecruitmentId(Long userId, Long recruitmentId);
}
