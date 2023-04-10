package com.moa.domain.notice;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByRecruitmentId(Long recruitmentId);

    Optional<Notice> findByIdAndRecruitmentId(Long noticeId, Long recruitmentId);

    @EntityGraph(attributePaths = {"attendMembers", "attendMembers.user"})
    Optional<Notice> findFetchMemberByIdAndRecruitmentId(Long noticeId, Long recruitmentId);
}
