package com.moa.domain.possible;

import com.moa.domain.member.ApplimentMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface PossibleTimeRepository extends JpaRepository<PossibleTime, Long> {
    List<PossibleTime> findAllByApplimentMemberId(Long applimentMemberId);

    @Modifying(clearAutomatically = true)
    void deleteAllByApplimentMember(ApplimentMember applimentMember);
}
