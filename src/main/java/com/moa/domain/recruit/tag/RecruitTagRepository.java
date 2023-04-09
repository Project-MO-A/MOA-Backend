package com.moa.domain.recruit.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitTagRepository extends JpaRepository<RecruitTag, RecruitTagId> {
    List<RecruitTag> findByTagNameIn(List<String> tagName);
}
