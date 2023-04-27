package com.moa.domain.recruit.tag;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RecruitTagRepository extends JpaRepository<RecruitTag, RecruitTagId> {
    void deleteAllByRecruitmentId(Long recruitmentId);
}
