package com.moa.domain.reply;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<Reply> findByRecruitmentIdOrderByParentIdAsc(Long recruitmentId);

    int countRepliesByRecruitmentId(Long recruitmentId);

    @EntityGraph(attributePaths = {"user"})
    Optional<Reply> findFetchUserById(Long id);
}
