package com.moa.domain.notice;

import com.moa.domain.base.BaseTimeEntity;
import com.moa.domain.recruit.Recruitment;
import com.moa.dto.notice.UpdateNoticeRequest;
import com.moa.global.exception.service.AssociationMisMatchException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

import static com.moa.global.exception.ErrorCode.NOTICE_ASSOCIATION_MISMATCH;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
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

    private void updateConfirmedTime(LocalDateTime meetingTime) {
        if (meetingTime != null) {
            this.confirmedTime = meetingTime;
        }
    }

    private void updateConfirmedLocation(String confirmedLocation) {
        if (StringUtils.hasText(confirmedLocation)) {
            this.confirmedLocation = confirmedLocation;
        }
    }

    private void updateCheckVote(Boolean checkVote) {
        if (checkVote != null && this.checkVote != checkVote) {
            this.checkVote = checkVote;
        }
    }
}
