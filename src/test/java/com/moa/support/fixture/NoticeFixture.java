package com.moa.support.fixture;

import com.moa.domain.notice.Notice;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.time.LocalDateTime;

public enum NoticeFixture {
    FIRST_NOTICE("첫 번째 공지", "공지사항 입니당", "강남 스터디카페", LocalDateTime.of(2023, 4, 1, 13, 0)),
    SECOND_NOTICE("두 번째 공지","공지사항 입니당", "강남 스터디카페", LocalDateTime.of(2023, 4, 8, 13, 0)),
    THIRD_NOTICE("세 번째 공지","공지사항 입니당",  "강남 스터디카페", LocalDateTime.of(2023, 4, 15, 13, 0)),
    FOURTH_NOTICE("네 번째 공지","공지사항 입니당", "강남 스터디카페", LocalDateTime.of(2023, 4, 22, 13, 0));

    private final String title;
    private final String content;
    private final String location;
    private final LocalDateTime time;

    NoticeFixture(String title, String content, String location, LocalDateTime time) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.time = time;
    }

    public Notice 투표_공지_생성(Recruitment recruitment) {
        return 기본_생성(recruitment, true);
    }

    public Notice 일반_공지_생성(Recruitment recruitment) {
        return 기본_생성(recruitment, false);
    }

    public Notice 기본_생성(Recruitment recruitment, boolean vote) {
        return Notice.builder()
                .post(new Post(this.title, this.content))
                .confirmedTime(this.time)
                .confirmedLocation(this.location)
                .recruitment(recruitment)
                .checkVote(vote)
                .build();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
