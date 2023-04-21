package com.moa.domain.member;

import com.moa.domain.recruit.Recruitment;
import com.moa.dto.member.ApplimentMemberResponse;
import com.moa.dto.member.ApprovedMemberResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.moa.domain.member.ApprovalStatus.APPROVED;
import static com.moa.domain.member.Attendance.ATTENDANCE;
import static com.moa.domain.member.QApplimentMember.applimentMember;
import static com.moa.domain.member.QAttendMember.attendMember;
import static com.moa.domain.member.QRecruitMember.recruitMember;
import static com.moa.domain.notice.QNotice.notice;
import static com.moa.domain.recruit.QRecruitment.recruitment;
import static com.moa.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class AdminRepository implements ApplimentSearchRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ApplimentMember> findApplimentMemberById(Long applyId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(applimentMember)
                .join(applimentMember.user).fetchJoin()
                .join(applimentMember.recruitMember).fetchJoin()
                .where(applimentMember.id.eq(applyId))
                .fetchFirst());
    }

    @Override
    public List<ApplimentMemberResponse> findAllApplimentMembers(final Long recruitmentId, final ApprovalStatus status) {
        return queryFactory
                .select(Projections.constructor(ApplimentMemberResponse.class,
                        user.id,
                        applimentMember.id,
                        user.nickname,
                        recruitMember.recruitField,
                        applimentMember.status)
                )
                .from(recruitment)
                .join(recruitment.members, recruitMember)
                .join(applimentMember).on(applimentMember.recruitMember.eq(recruitMember))
                .join(user).on(applimentMember.user.eq(user))
                .where(recruitment.id.eq(recruitmentId),
                        statusEq(status))
                .fetch();
    }

    @Override
    public List<ApprovedMemberResponse> findAllApprovedMembers(final Long recruitmentId) {
        List<ApprovedMemberResponse> approvedMemberResponses = queryFactory
                .select(Projections.constructor(ApprovedMemberResponse.class,
                        user.id,
                        applimentMember.id,
                        user.nickname,
                        recruitMember.id,
                        recruitMember.recruitField,
                        applimentMember.popularity)
                )
                .from(recruitment)
                .join(recruitment.members, recruitMember)
                .join(applimentMember).on(applimentMember.recruitMember.eq(recruitMember))
                .join(user).on(applimentMember.user.eq(user))
                .where(recruitment.id.eq(recruitmentId),
                        statusEq(APPROVED))
                .fetch();

        for (ApprovedMemberResponse approvedMembers : approvedMemberResponses) {
            approvedMembers.setTotalAttend(countAttend(recruitmentId, approvedMembers.getUserId(), null));
            approvedMembers.setAttend(countAttend(recruitmentId, approvedMembers.getUserId(), ATTENDANCE));
        }

        return approvedMemberResponses;
    }

    @Override
    public Optional<Recruitment> findRecruitmentById(final Long recruitmentId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(recruitment)
                .where(recruitment.id.eq(recruitmentId))
                .fetchOne());
    }

    public Long countAttend(final Long recruitmentId, final Long userId, final Attendance attendance) {
        return queryFactory
                .select(attendMember.count())
                .from(recruitment)
                .join(notice).on(notice.recruitment.id.eq(recruitmentId))
                .join(attendMember).on(attendMember.notice.eq(notice))
                .where(attendMember.user.id.eq(userId),
                        attendanceEq(attendance))
                .fetchOne();
    }

    private BooleanExpression attendanceEq(Attendance attendance) {
        return attendance != null ? attendMember.attendance.eq(ATTENDANCE) : null;
    }

    private BooleanExpression statusEq(ApprovalStatus status) {
        return status != null ? applimentMember.status.eq(status) : null;
    }
}
