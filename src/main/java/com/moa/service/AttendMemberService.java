package com.moa.service;

import com.moa.domain.member.AttendMember;
import com.moa.domain.member.AttendMemberRepository;
import com.moa.dto.notice.VoteAttendanceRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.global.exception.service.VoteFinishException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.moa.global.exception.ErrorCode.ATTENDMEMBER_NOT_FOUND;
import static com.moa.global.exception.ErrorCode.NOTICE_VOTE_FINISH;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendMemberService {

    private final AttendMemberRepository attendMemberRepository;

    public Long voteAttendance(VoteAttendanceRequest request) {
        AttendMember attendMember = attendMemberRepository.findByNoticeIdAndUserId(request.noticeId(), request.userId())
                .orElseThrow(() -> new EntityNotFoundException(ATTENDMEMBER_NOT_FOUND));

        if (!attendMember.canVote()) {
            throw new VoteFinishException(NOTICE_VOTE_FINISH);
        }
        attendMember.checkRecruitment(request.recruitmentId());
        attendMember.changeAttendance(request.attendance());
        return attendMember.getId();
    }
}
