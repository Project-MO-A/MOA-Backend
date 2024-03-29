package com.moa.support.fixture;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.Category;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.user.Popularity;
import com.moa.domain.user.User;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.moa.domain.recruit.Category.*;
import static com.moa.domain.recruit.RecruitStatus.RECRUITING;

public enum RecruitmentFixture {
    PROGRAMMING_POST("사이드 프로젝트 모집합니다.",
            "사이드 프로젝트 같이하실 프론트, 백엔드 개발자를 모집합니다", PROGRAMMING),
    LANGUAGE_POST("토익 스터디 인원 모집합니다",
            "토익 950점 목표로 열심히 공부하실 스터디원을 모집합니다", LANGUAGE),
    HOBBY_POST("독서 스터디 하실분 구합니다",
            "같이 책 읽으며 학습하실 독서 스터디원 구합니다.", HOBBY),
    ;

    private final String title;
    private final String content;
    private final Category category;

    RecruitmentFixture(final String title, final String content, final Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public Recruitment 생성(final User postUser, final List<RecruitTag> recruitTags, List<RecruitMember> recruitMembers) {
        Recruitment recruitment = 기본_엔티티_생성(postUser);
        recruitment.setTags(recruitTags);
        recruitment.setMembers(recruitMembers);
        return recruitment;
    }

    public Recruitment 생성1(final User postUser, final List<RecruitTag> recruitTags, List<RecruitMember> recruitMembers) {
        Recruitment recruitment = 기본_엔티티_생성(postUser);
        recruitment.setTags(recruitTags);
        List<RecruitMember> test = new ArrayList<>();
        for (int i = 0; i < recruitMembers.size(); i++) {
            test.add(new TestRecruitMember(recruitment, recruitMembers.get(i), (long) i));
        }
        recruitment.setMembers(test);
        return recruitment;
    }

    public Recruitment 아이디를_삽입하여_생성(Long recruitId) {

        Recruitment recruitment = 기본_빌더_생성().build();
        Field id = null;
        try {
            id = recruitment.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(recruitment, recruitId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return recruitment;
    }

    public Recruitment 생성() {
        return 기본_빌더_생성().build();
    }

    private Recruitment 기본_엔티티_생성(final User postUser) {
        return 기본_빌더_생성()
                .user(postUser)
                .build();
    }

    private Recruitment.RecruitmentBuilder 기본_빌더_생성() {
        return Recruitment.builder()
                .post(new Post(this.title, this.content))
                .status(RECRUITING)
                .category(this.category);
    }

    static class TestRecruitMember extends RecruitMember {
        private Long id;

        public TestRecruitMember(Recruitment recruitment, RecruitMember recruitMember, Long id) {
            super(recruitment, recruitMember.getRecruitField(), recruitMember.getTotalRecruitCount());
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Category getCategory() {
        return category;
    }
}
