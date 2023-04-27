package com.moa.support.fixture;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.possible.PossibleTime;

import java.time.LocalDateTime;

public enum PossibleTimeFixture {
    TIME1(LocalDateTime.of(2023, 4, 20, 9, 0),
            LocalDateTime.of(2023, 4, 20, 18, 0)),
    TIME2(LocalDateTime.of(2023, 4, 21, 9, 0),
            LocalDateTime.of(2023, 4, 21, 17, 0)),
    TIME3(LocalDateTime.of(2023, 4, 22, 9, 0),
            LocalDateTime.of(2023, 4, 22, 19, 0)),
    TIME4(LocalDateTime.of(2023, 4, 23, 9, 0),
            LocalDateTime.of(2023, 4, 23, 19, 0)),
    TIME5(LocalDateTime.of(2023, 4, 24, 9, 0),
            LocalDateTime.of(2023, 4, 24, 20, 0)),
    TIME6(LocalDateTime.of(2023, 4, 25, 9, 0),
            LocalDateTime.of(2023, 4, 25, 11, 0)),
    TIME7(LocalDateTime.of(2023, 4, 26, 9, 0),
            LocalDateTime.of(2023, 4, 26, 15, 0)),
    TIME8(LocalDateTime.of(2023, 4, 27, 9, 0),
            LocalDateTime.of(2023, 4, 27, 15, 0));

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    PossibleTimeFixture(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public PossibleTime 생성(ApplimentMember applimentMember) {
        return 기본_빌더_생성(applimentMember)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .build();
    }

    public PossibleTime 시간을_바꾸어_생성(ApplimentMember applimentMember, LocalDateTime startTime, LocalDateTime endTime) {
        return 기본_빌더_생성(applimentMember)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public PossibleTime 빈_객체_생성() {
        return PossibleTime.builder()
                .startTime(this.startTime)
                .endTime(this.endTime)
                .build();
    }

    private PossibleTime.PossibleTimeBuilder 기본_빌더_생성(ApplimentMember applimentMember) {
        return PossibleTime.builder()
                .startTime(this.startTime)
                .endTime(this.endTime)
                .applimentMember(applimentMember);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
