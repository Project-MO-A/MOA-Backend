package com.moa.domain.notice;

import com.moa.domain.recruit.Recruitment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUIMENT_ID")
    private Recruitment recruitment;

    @Embedded
    private Post post;
    private LocalDateTime confirmedTime;
    private String confirmedLocation;

    @Builder
    public Notice(Recruitment recruitment, Post post, LocalDateTime confirmedTime, String confirmedLocation) {
        this.recruitment = recruitment;
        this.post = post;
        this.confirmedTime = confirmedTime;
        this.confirmedLocation = confirmedLocation;
    }
}
