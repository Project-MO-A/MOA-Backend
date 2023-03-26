package com.moa.domain.member;

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
public class ApplimentRepository implements ApplimentSearchRepository {
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

        Long totalVote = countTotalVote(recruitmentId);

        for (ApprovedMemberResponse approvedMembers : approvedMemberResponses) {
            approvedMembers.setTotalVote(totalVote);
            approvedMembers.setAttend(countAttend(recruitmentId, approvedMembers.getUserId()));
        }

        return approvedMemberResponses;
    }

    public Long countTotalVote(final Long recruitmentId) {
        return queryFactory
                .select(notice.count())
                .from(notice)
                .where(notice.recruitment.id.eq(recruitmentId),
                        notice.checkVote.isTrue())
                .fetchOne();
    }

    public Long countAttend(final Long recruitmentId, final Long userId) {
        return queryFactory
                .select(attendMember.count())
                .from(recruitment)
                .join(notice).on(notice.recruitment.id.eq(recruitmentId))
                .join(attendMember).on(attendMember.notice.eq(notice))
                .where(attendMember.user.id.eq(userId),
                        attendMember.attendance.eq(ATTENDANCE))
                .fetchOne();
    }

    private BooleanExpression statusEq(ApprovalStatus status) {
        return status != null ? applimentMember.status.eq(status) : null;
    }
}
