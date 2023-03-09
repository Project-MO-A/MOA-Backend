package com.moa.domain.possible;

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
    @JoinColumn(name = "TIME_SELECTOR_ID")
    private PossibleTimeSelector selector;

    @Column(name = "possible_day")
    private String day;
    @Column(nullable = false)
    private int startTime;

    @Column(nullable = false)
    private int endTime;

    @Builder
    public PossibleTime(PossibleTimeSelector selector, String day, int startTime, int endTime) {
        this.selector = selector;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
