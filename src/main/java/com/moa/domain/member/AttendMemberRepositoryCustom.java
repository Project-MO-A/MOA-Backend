package com.moa.domain.member;

import com.moa.domain.notice.Notice;

public interface AttendMemberRepositoryCustom {
    void saveFromApplimentMember(Notice notice, Long recruitmentId);
}
