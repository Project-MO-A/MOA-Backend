package com.moa.domain.notice;

import com.moa.domain.base.BaseTimeEntity;
import com.moa.domain.recruit.Recruitment;
import com.moa.dto.notice.UpdateNoticeRequest;
import com.moa.global.exception.service.AssociationMisMatchException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.moa.global.exception.ErrorCode.NOTICE_ASSOCIATION_MISMATCH;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notice extends BaseTimeEntity {
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
    private boolean checkVote;

    @Builder
    public Notice(Recruitment recruitment, Post post, LocalDateTime confirmedTime, String confirmedLocation, boolean checkVote) {
        this.recruitment = recruitment;
        this.post = post;
        this.confirmedTime = confirmedTime;
        this.confirmedLocation = confirmedLocation;
        this.checkVote = checkVote;
    }

    public void update(Recruitment recruitment, UpdateNoticeRequest request) {
        if (this.recruitment != recruitment) {
            throw new AssociationMisMatchException(NOTICE_ASSOCIATION_MISMATCH);
        }
        this.post.updateTitle(request.title());
        this.post.updateContent(request.content());
        updateConfirmedTime(request.meetingTime());
        updateConfirmedLocation(request.confirmedLocation());
        updateCheckVote(request.checkVote());
    }

    private void updateConfirmedTime(String meetingTime) {
        if (confirmedLocation != null) {
            this.confirmedLocation = meetingTime;
        }
    }

    private void updateConfirmedLocation(String confirmedLocation) {
        if (confirmedLocation != null) {
            this.confirmedLocation = confirmedLocation;
        }
    }

    private void updateCheckVote(boolean checkVote) {
        if (this.checkVote != checkVote) {
            this.checkVote = checkVote;
        }
    }
}
