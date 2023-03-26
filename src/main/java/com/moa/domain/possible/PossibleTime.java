package com.moa.domain.possible;

import com.moa.domain.member.ApplimentMember;
import com.moa.global.exception.service.InvalidTimeException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import static com.moa.global.exception.ErrorCode.TIME_INVALID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PossibleTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSSIBLE_TIME_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLIMENT_MEMBER_ID")
    private ApplimentMember applimentMember;

    @Column(name = "possible_day")
    private Day day;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Builder
    public PossibleTime(ApplimentMember applimentMember, Day day, LocalTime startTime, LocalTime endTime) {
        this.applimentMember = applimentMember;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        validateTime();
    }

    private void validateTime() {
        if (this.startTime.getHour() > this.endTime.getHour() ||
                (this.startTime.getHour() == this.endTime.getHour() && this.startTime.getMinute() >= this.endTime.getMinute())) {
            throw new InvalidTimeException(TIME_INVALID);
        }
    }
}
