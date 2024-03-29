package com.moa.domain.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

public interface AttendMemberRepository extends JpaRepository<AttendMember, Long>, AttendMemberRepositoryCustom {

    @EntityGraph(attributePaths = {"user"}, type = LOAD)
    List<AttendMember> findAllByNoticeIdIn(Collection<Long> noticeId);

    @EntityGraph(attributePaths = {"notice"})
    Optional<AttendMember> findByNoticeIdAndUserId(Long noticeId, Long userId);
}
