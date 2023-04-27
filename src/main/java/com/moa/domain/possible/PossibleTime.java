package com.moa.domain.possible;

import com.moa.domain.member.ApplimentMember;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@ToString
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

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Builder
    public PossibleTime(ApplimentMember applimentMember, LocalDateTime startTime, LocalDateTime endTime) {
        this.applimentMember = applimentMember;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
