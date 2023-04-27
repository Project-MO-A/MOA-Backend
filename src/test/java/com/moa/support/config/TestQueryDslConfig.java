package com.moa.support.config;

import com.moa.domain.member.AdminRepository;
import com.moa.domain.recruit.RecruitmentSearchRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@TestConfiguration
public class TestQueryDslConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public AdminRepository adminRepository(JPAQueryFactory jpaQueryFactory) {
        return new AdminRepository(jpaQueryFactory);
    }

    @Bean
    public RecruitmentSearchRepository recruitmentSearchRepository(JPAQueryFactory jpaQueryFactory) {
        return new RecruitmentSearchRepository(jpaQueryFactory);
    }
}
