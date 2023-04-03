package com.moa.support.fixture;

import com.moa.dto.member.RecruitMemberRequest;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.recruit.RecruitUpdateRequest;
import lombok.Getter;

import java.util.List;

import static com.moa.support.fixture.TagFixture.BACKEND_TAG;
import static com.moa.support.fixture.TagFixture.FRONTEND_TAG;

@Getter
public enum RecruitRequestFixture {
    BASIC_REQUEST("프로젝트 모집", "프로젝트 인원 모집합니다.", BACKEND_TAG.getTags(), List.of(
            RecruitMemberRequest.builder().field("백엔드").total(5).build(),
            RecruitMemberRequest.builder().field("프론트").total(4).build()
    )),
    ANOTHER_REQUEST("사이드 프로젝트 모집", "개발자 모집합니다.", FRONTEND_TAG.getTags(), List.of(
            RecruitMemberRequest.builder().field("백엔드").total(4).build(),
            RecruitMemberRequest.builder().field("프론트").total(3).build()
    ));

    private final String title;
    private final String content;
    private final List<String> tags;
    private final List<RecruitMemberRequest> requests;

    RecruitRequestFixture(String title, String content, List<String> tag, List<RecruitMemberRequest> recruitMemberRequests) {
        this.title = title;
        this.content = content;
        this.tags = tag;
        this.requests = recruitMemberRequests;
    }

    public RecruitPostRequest 등록_생성() {
        return 기본_등록_빌더()
                .build();
    }

    public RecruitPostRequest 제목을_변경하여_등록_생성(String title) {
        return 기본_등록_빌더()
                .title(title)
                .build();
    }

    public RecruitPostRequest 멤버를_변경하여_등록_생성(List<RecruitMemberRequest> requests) {
        return 기본_등록_빌더()
                .memberFields(requests)
                .build();
    }

    public RecruitUpdateRequest 수정_생성() {
        return 기본_수정_빌더()
                .build();
    }

    public RecruitUpdateRequest 멤버를_변경하여_수정_생성(List<RecruitMemberRequest> requests) {
        return 기본_수정_빌더()
                .memberFields(requests)
                .build();
    }

    private RecruitPostRequest.RecruitPostRequestBuilder 기본_등록_빌더() {
        return RecruitPostRequest.builder()
                .title(this.title)
                .content(this.content)
                .tags(this.tags)
                .memberFields(this.requests);
    }

    private RecruitUpdateRequest.RecruitUpdateRequestBuilder 기본_수정_빌더() {
        return RecruitUpdateRequest.builder()
                .title(this.title)
                .content(this.content)
                .tags(this.tags)
                .memberFields(this.requests);
    }
}
