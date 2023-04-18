package com.moa.domain.possible;

import com.moa.domain.member.ApplimentMember;
import com.moa.global.exception.service.InvalidTimeException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private LocalDateTime time;

    @Builder
    public PossibleTime(ApplimentMember applimentMember, LocalDateTime time) {
        this.applimentMember = applimentMember;
        this.time = time;
    }
}
