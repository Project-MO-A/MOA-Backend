package com.moa.domain.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecruitMemberRepository extends JpaRepository<RecruitMember, Long> {
    RecruitMember findByRecruitFieldAndRecruitmentId(String recruitField, Long recruitmentId);

    @EntityGraph(attributePaths = {"recruitment"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select r from RecruitMember r where r.id in :id")
    List<RecruitMember> findFetchAllById(@Param("id") Iterable<Long> Id);
}
