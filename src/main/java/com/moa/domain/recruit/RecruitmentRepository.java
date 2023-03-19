package com.moa.domain.recruit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    @Query("select r from Recruitment r join fetch r.user where r.id = :id")
    Optional<Recruitment> findByIdFetchUser(@Param("id") Long recruitId);
}
