package com.moa.support;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.possible.Day;
import com.moa.domain.possible.PossibleTime;
import lombok.Getter;

import java.time.LocalTime;

import static com.moa.domain.possible.Day.*;

@Getter
public enum PossibleTimeFixture {
    MONDAY_ALL(MONDAY, 9, 23),
    MONDAY_DAYTIME(MONDAY, 13, 19),
    TUESDAY_ALL(TUESDAY, 9, 23),
    TUESDAY_DAYTIME(TUESDAY, 13, 19),
    SATURDAY_ALL(SATURDAY, 9, 23),
    SATURDAY_DAYTIME(SATURDAY, 13, 19),
    SUNDAY_ALL(SUNDAY, 9, 23),
    SUNDAY_DAYTIME(SUNDAY, 13, 19);

    private final Day day;
    private final int startHour;
    private final int endHour;

    PossibleTimeFixture(Day day, int startHour, int endHour) {
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public PossibleTime 생성(ApplimentMember applimentMember) {
        return 기본_빌더_생성(applimentMember)
                .startTime(LocalTime.of(this.startHour, 0))
                .endTime(LocalTime.of(this.endHour, 0))
                .build();
    }

    public PossibleTime 시간을_바꾸어_생성(ApplimentMember applimentMember, int startHour, int endHour) {
        return 기본_빌더_생성(applimentMember)
                .startTime(LocalTime.of(startHour, 0))
                .endTime(LocalTime.of(endHour, 0))
                .build();
    }

    private PossibleTime.PossibleTimeBuilder 기본_빌더_생성(ApplimentMember applimentMember) {
        return PossibleTime.builder()
                .applimentMember(applimentMember);
    }
}
