package com.moa.domain.possible;

import com.moa.domain.member.ApplimentMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PossibleTimeRepository extends JpaRepository<PossibleTime, Long> {
    @Query("select p from PossibleTime p where p.applimentMember.id=:applyId")
    List<PossibleTime> findAllByApplyId(@Param("applyId") Long applyId);

    @Modifying(clearAutomatically = true)
    void deleteAllByApplimentMember(ApplimentMember applimentMember);
}
