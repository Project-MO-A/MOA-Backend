package com.moa.domain.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplimentMemberRepository extends JpaRepository<ApplimentMember, Long> {
    @EntityGraph(attributePaths = {"user", "user.interests", "recruitMember", "recruitMember.recruitment"},
            type = EntityGraph.EntityGraphType.LOAD)
    List<ApplimentMember> findAllByUserId(Long userId);

    @Query("select a from ApplimentMember a join fetch a.user join a.recruitMember where a.id =:applyId")
    Optional<ApplimentMember> findFetchById(@Param(value = "applyId") Long applyId);
}
