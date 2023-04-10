package com.moa.domain.recruit;

import com.moa.domain.recruit.tag.RecruitTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    @Query("select r from Recruitment r join fetch r.user join fetch r.tags s join fetch s.tag where r.id = :id")
    Optional<Recruitment> findFetchUserTagsById(@Param("id") Long recruitId);

    @Query("select r from Recruitment r join fetch r.members where r.id = :id")
    Optional<Recruitment> findFetchMembersById(@Param("id") Long recruitId);

    @Query("select r from Recruitment r join fetch r.user u where u.id = :id")
    List<Recruitment> findAllFetchUserById(@Param("id") Long userId);

    @Query("select r from Recruitment r join fetch r.tags s join fetch s.tag where r.id = :id")
    List<Recruitment> findFetchTagsByUserId(@Param("id") Long userId);

    @Query("""
            select r
            from Recruitment r
            join fetch r.user
            where r.id in (
                select ri.recruitment.id
                from RecruitmentInterest ri
                group by ri.recruitment.id
                order by count(1) desc
            )""")
    List<Recruitment> findAllDescByCount(Pageable limit);

    List<Recruitment> findByTagsIn(List<RecruitTag> tags);
}
