package com.moa.domain.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplimentMemberRepository extends JpaRepository<ApplimentMember, Long> {
    @EntityGraph(attributePaths = {"user", "recruitMember"}, type = EntityGraph.EntityGraphType.LOAD)
    List<ApplimentMember> findAllByUserId(Long userId);
}
