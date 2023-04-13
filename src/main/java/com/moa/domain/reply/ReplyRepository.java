package com.moa.domain.reply;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<Reply> findByRecruitmentIdOrderByParentIdAsc(Long recruitmentId);
}
