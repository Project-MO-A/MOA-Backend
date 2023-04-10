package com.moa.domain.recruit;

import com.moa.domain.base.OrderByNull;
import com.moa.domain.base.SearchParam;
import com.moa.domain.base.SearchRepository;
import com.moa.dto.recruit.RecruitmentInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.moa.domain.base.SearchParam.*;
import static com.moa.domain.member.QRecruitMember.recruitMember;
import static com.moa.domain.recruit.QRecruitment.recruitment;
import static com.moa.domain.recruit.tag.QRecruitTag.recruitTag;
import static com.moa.domain.recruit.tag.QTag.tag;
import static com.moa.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class RecruitmentSearchRepository implements SearchRepository<RecruitmentInfo> {
    private final JPAQueryFactory queryFactory;

    @Override
    public RecruitmentInfo searchOne(Map<String, String> searchCondition) {
        RecruitmentInfo recruitmentInfo = getSearchQuery(searchCondition)
                .fetchFirst();
        setTags(recruitmentInfo);
        return recruitmentInfo;
    }

    @Override
    public List<RecruitmentInfo> searchAll(Map<String, String> searchCondition, Pageable pageable) {
        List<RecruitmentInfo> infoList = getSearchQuery(searchCondition)
                .fetch();

        for (RecruitmentInfo recruitmentInfo : infoList) {
            setTags(recruitmentInfo);
        }
        return infoList;
    }

    @Override
    public List<RecruitmentInfo> searchAll(Map<String, String> searchCondition, Pageable pageable,  String orderField, String direction) {
        List<RecruitmentInfo> infoList = getSearchQuery(searchCondition)
                .orderBy(getOrderSpecifier(orderField, direction))
                .fetch();

        for (RecruitmentInfo recruitmentInfo : infoList) {
            setTags(recruitmentInfo);
        }
        return infoList;
    }

    private JPAQuery<RecruitmentInfo> getSearchQuery(Map<String, String> searchParameter) {
        return queryFactory
                .select(Projections.constructor(RecruitmentInfo.class,
                                recruitment.id,
                                recruitment.post.title,
                                recruitment.user.nickname,
                                recruitment.createdDate,
                                recruitment.status,
                                recruitment.category,
                                recruitMember.totalRecruitCount,
                                recruitMember.currentRecruitCount
                        )
                )
                .from(recruitment)
                .join(recruitment.user, user).fetchJoin()
                .join(recruitment.members, recruitMember)
                .where(allCond(searchParameter));
    }

    private OrderSpecifier getOrderSpecifier(String orderField, String direction) {
        if (!StringUtils.hasText(orderField) || !StringUtils.hasText(direction)) {
            return OrderByNull.getDefault();
        }

        if (orderField.equals(CREATE_DATE.getParamKey())) {
            return new OrderSpecifier<>(SearchParam.getOrder(direction), recruitment.createdDate);
        }
        if (orderField.equals(MODIFIED_DATE.getParamKey())) {
            return new OrderSpecifier<>(SearchParam.getOrder(direction), recruitment.lastModifiedDate);
        }

        return OrderByNull.getDefault();
    }

    private void setTags(RecruitmentInfo recruitmentInfo) {
        if (Objects.isNull(recruitmentInfo)) return;
        List<String> tags = getTags(recruitmentInfo.getId());
        recruitmentInfo.setTags(tags);
    }

    private List<String> getTags(Long recruitId) {
        return queryFactory
                .select(tag.name)
                .from(tag)
                .join(recruitTag).on(recruitTag.tag.eq(tag))
                .join(recruitTag.recruitment, recruitment)
                .where(recruitment.id.eq(recruitId))
                .fetch();
    }

    private BooleanBuilder allCond(Map<String, String> searchParameter) {
        BooleanBuilder builder = new BooleanBuilder();

        return builder
                .and(titleLike(searchParameter.getOrDefault(TITLE.getParamKey(), null)))
                .and(categoryEq(searchParameter.getOrDefault(CATEGORY.getParamKey(), null)))
                .and(tagLike(searchParameter.getOrDefault(TAG.getParamKey(), null)))
                .and(stateEq(Integer.parseInt(searchParameter.getOrDefault(STATE_CODE.getParamKey(), null))));
    }

    private BooleanExpression titleEq(String title) {
        return StringUtils.hasText(title) ? recruitment.post.title.eq(title) : null;
    }

    private BooleanExpression titleLike(String title) {
        return StringUtils.hasText(title) ? recruitment.post.title.contains(title) : null;
    }

    private BooleanExpression tagLike(String tagName) {
        if (!StringUtils.hasText(tagName)) return null;
        List<Long> recruitIdList = queryFactory
                .select(recruitment.id)
                .from(recruitment)
                .join(recruitTag).on(recruitTag.recruitment.eq(recruitment))
                .join(recruitTag.tag, tag)
                .where(recruitTag.tag.name.contains(tagName))
                .fetch();
        return StringUtils.hasText(tagName) ? recruitment.id.in(recruitIdList) : null;
    }

    private BooleanExpression categoryEq(String category) {
        Category instance = Category.getInstance(category);
        return recruitment.category.eq(instance);
    }

    private BooleanExpression stateEq(Integer stateCode) {
        RecruitStatus instance = RecruitStatus.getInstance(stateCode);
        return recruitment.status.eq(instance);
    }
}
