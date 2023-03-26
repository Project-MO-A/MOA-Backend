package com.moa.domain.possible;

import com.moa.domain.member.ApplimentMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private int startTime;

    @Column(nullable = false)
    private int endTime;

    @Builder
    public PossibleTime(ApplimentMember applimentMember, Day day, int startTime, int endTime) {
        this.applimentMember = applimentMember;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
