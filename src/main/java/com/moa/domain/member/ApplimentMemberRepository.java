package com.moa.domain.member;

import com.moa.domain.recruit.Recruitment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplimentMemberRepository extends JpaRepository<ApplimentMember, Long> {
    @Query("""
            select a
            from ApplimentMember a
            join fetch a.recruitMember rm
            join fetch rm.recruitment r
            where a.user.id = :userId
            and rm.recruitField <> 'LEADER'
            """)
    List<ApplimentMember> findAllRecruitmentByUserId(@Param("userId") Long userId);

    @Query("""
            select a
            from ApplimentMember a
            join fetch a.recruitMember rm
            join fetch rm.recruitment r
            where a.user.id = :userId
            """)
    List<ApplimentMember> findAllRecruitmentForAuthByUserId(@Param("userId") Long userId);

    @Query("select a from ApplimentMember a join a.recruitMember m join a.user u where m.recruitment.id=:recruitId and u.id=:userId")
    Optional<ApplimentMember> findByRecruitIdAndUserId(@Param("recruitId") Long recruitmentId, @Param("userId") Long userId);
}
