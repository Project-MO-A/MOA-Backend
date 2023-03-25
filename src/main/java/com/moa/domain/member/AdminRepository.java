package com.moa.domain.member;

import com.moa.dto.member.ApplimentMemberResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.moa.domain.member.QApplimentMember.applimentMember;
import static com.moa.domain.member.QRecruitMember.recruitMember;
import static com.moa.domain.recruit.QRecruitment.recruitment;
import static com.moa.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class AdminRepository {
    private final JPAQueryFactory queryFactory;

    public List<ApplimentMemberResponse> findAllApplimentResponse(final Long recruitmentId, final ApprovalStatus status) {
        return queryFactory
                .select(Projections.constructor(ApplimentMemberResponse.class,
                        user.id,
                        applimentMember.id,
                        user.nickname,
                        recruitMember.recruitField,
                        applimentMember.status)
                )
                .from(recruitment)
                .join(recruitMember).on(recruitMember.recruitment.eq(recruitment))
                .join(applimentMember).on(applimentMember.recruitMember.eq(recruitMember))
                .join(user).on(applimentMember.user.eq(user))
                .where(recruitment.id.eq(recruitmentId),
                        statusEq(status))
                .fetch();
    }
    private BooleanExpression statusEq(ApprovalStatus status) {
        return status != null ? applimentMember.status.eq(status) : null;
    }
}
