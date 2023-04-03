package com.moa.domain.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecruitMemberRepository extends JpaRepository<RecruitMember, Long> {
    Optional<RecruitMember> findByRecruitFieldAndRecruitmentId(String recruitField, Long recruitmentId);

    @EntityGraph(attributePaths = {"recruitment"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select r from RecruitMember r where r.id in :id")
    List<RecruitMember> findFetchAllById(@Param("id") Iterable<Long> Id);
}
