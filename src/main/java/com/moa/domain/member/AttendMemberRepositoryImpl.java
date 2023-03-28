package com.moa.domain.member;

import com.moa.domain.notice.Notice;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import static com.moa.domain.member.Approval.APPROVED;
import static com.moa.domain.member.Attendance.NONE;

@RequiredArgsConstructor
public class AttendMemberRepositoryImpl implements AttendMemberRepositoryCustom {
    private final EntityManager em;

    @Override
    public void saveFromApplimentMember(Notice notice, Long recruitmentId) {
        em.createQuery("insert into AttendMember (attendance, user, notice) " +
                        "select :attendance, am.user, :notice " +
                        "from ApplimentMember am " +
                        "left join am.recruitMember rm " +
                        "where am.approval = :approval " +
                        "and rm.recruitment.id = :recruitmentId")
                .setParameter("attendance", NONE)
                .setParameter("notice", notice)
                .setParameter("approval", APPROVED)
                .setParameter("recruitmentId", recruitmentId)
                .executeUpdate();
    }
}
