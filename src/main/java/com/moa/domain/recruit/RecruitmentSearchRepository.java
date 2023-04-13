package com.moa.domain.recruit;

import com.moa.domain.base.OrderByNull;
import com.moa.domain.base.SearchRepository;
import com.moa.dto.recruit.RecruitmentInfo;
import com.moa.global.exception.service.NumberFormatException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.moa.domain.base.SearchParam.*;
import static com.moa.domain.member.QRecruitMember.recruitMember;
import static com.moa.domain.recruit.QRecruitment.recruitment;
import static com.moa.domain.recruit.tag.QRecruitTag.recruitTag;
import static com.moa.domain.recruit.tag.QTag.tag;
import static com.moa.domain.user.QUser.user;
import static com.moa.global.exception.ErrorCode.NUMBER_FORMAT;

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
    public Page<RecruitmentInfo> searchPage(Map<String, String> searchCondition, Pageable pageable) {
        List<RecruitmentInfo> infoList = getSearchQuery(searchCondition)
                .orderBy(getOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        for (RecruitmentInfo recruitmentInfo : infoList) {
            setTags(recruitmentInfo);
        }

        return getPage(searchCondition, pageable, infoList);
    }

    @Override
    public Slice<RecruitmentInfo> searchSlice(Map<String, String> searchCondition, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<RecruitmentInfo> infoList = getSearchQuery(searchCondition)
                .orderBy(getOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = isHasNext(pageSize, infoList);
        for (RecruitmentInfo recruitmentInfo : infoList) {
            setTags(recruitmentInfo);
        }

        return new SliceImpl<>(infoList, pageable, hasNext);
    }

    private JPAQuery<RecruitmentInfo> getSearchQuery(Map<String, String> searchCondition) {
        return queryFactory
                .select(Projections.constructor(RecruitmentInfo.class,
                                recruitment.id,
                                recruitment.post.title,
                                recruitment.user.nickname,
                                recruitment.createdDate,
                                recruitment.status,
                                recruitment.category,
                                recruitMember.totalRecruitCount.sum(),
                                recruitMember.currentRecruitCount.sum()
                        )
                )
                .from(recruitment)
                .join(recruitment.user, user)
                .join(recruitment.members, recruitMember)
                .where(allCond(searchCondition))
                .groupBy(recruitment.id);
    }

    private Page<RecruitmentInfo> getPage(Map<String, String> searchCondition, Pageable pageable, List<RecruitmentInfo> infoList) {
        JPAQuery<Long> countQuery = queryFactory
                .select(recruitment.count())
                .from(recruitment)
                .where(allCond(searchCondition));

        return PageableExecutionUtils.getPage(infoList, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier getOrderSpecifier(Pageable pageable) {
        Sort.Order createDateOrder = pageable.getSort().getOrderFor(CREATED_DATE.getParamKey());
        Sort.Order modifiedDateOrder = pageable.getSort().getOrderFor(MODIFIED_DATE.getParamKey());

        if (Objects.isNull(createDateOrder) && Objects.isNull(modifiedDateOrder)) {
            return OrderByNull.getDefault();
        }

        if (!Objects.isNull(createDateOrder)) {
            Order order = getOrder(createDateOrder.getDirection().name());
            return new OrderSpecifier<>(order, recruitment.createdDate);
        }
        Order order = getOrder(modifiedDateOrder.getDirection().name());
        return new OrderSpecifier<>(order, recruitment.lastModifiedDate);
    }

    private void setTags(RecruitmentInfo recruitmentInfo) {
        if (Objects.isNull(recruitmentInfo)) return;

        List<String> tags = getTags(recruitmentInfo.getId());
        recruitmentInfo.setTags(tags);
    }

    private boolean isHasNext(int pageSize, List<RecruitmentInfo> infoList) {
        if (infoList.size() > pageSize) {
            infoList.remove(pageSize);
            return true;
        }
        return false;
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
                .and(stateEq(searchParameter.getOrDefault(STATE_CODE.getParamKey(), null)))
                .and(withInDays(searchParameter.getOrDefault(DAYS_AGO.getParamKey(), null)));
    }

    private BooleanExpression withInDays(String daysAgo) {
        if (Objects.isNull(daysAgo)) return null;

        Long days = convertLong(daysAgo);
        LocalDateTime now = LocalDateTime.now();
        LocalDate startDate = now.toLocalDate().minusDays(days);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);

        return recruitment.createdDate.goe(startDateTime);
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
        if (Objects.isNull(category)) return null;

        Category instance = Category.getInstance(category);
        return recruitment.category.eq(instance);
    }

    private BooleanExpression stateEq(String stateCode) {
        if (Objects.isNull(stateCode)) return null;

        Integer code = convertInteger(stateCode);
        RecruitStatus instance = RecruitStatus.getInstance(code);
        return recruitment.status.eq(instance);
    }

    private Long convertLong(String number) {
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException numberFormatException) {
            throw new NumberFormatException(NUMBER_FORMAT);
        }
    }

    private Integer convertInteger(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException numberFormatException) {
            throw new NumberFormatException(NUMBER_FORMAT);
        }
    }
}