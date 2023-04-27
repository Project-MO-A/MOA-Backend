package com.moa.domain.notice;

import com.moa.domain.base.BaseTimeEntity;
import com.moa.domain.member.AttendMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.dto.notice.UpdateNoticeRequest;
import com.moa.global.exception.service.AssociationMisMatchException;
import com.moa.service.util.GrahamUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Double recommendedLocationX;
    private Double recommendedLocationY;
    private boolean checkVote;
    private boolean isVote;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttendMember> attendMembers = new ArrayList<>();
    @Builder
    public Notice(Recruitment recruitment, Post post, LocalDateTime confirmedTime, String confirmedLocation, Double recommendedLocationX, Double recommendedLocationY, boolean checkVote) {
        this.recruitment = recruitment;
        this.post = post;
        this.confirmedTime = confirmedTime;
        this.confirmedLocation = confirmedLocation;
        this.recommendedLocationX = recommendedLocationX;
        this.recommendedLocationY = recommendedLocationY;
        this.checkVote = checkVote;
        this.isVote = false;
    }

    public void update(Recruitment recruitment, UpdateNoticeRequest request) {
        if (this.recruitment != recruitment) {
            throw new AssociationMisMatchException(NOTICE_ASSOCIATION_MISMATCH);
        }
        this.post.updateContent(request.content());
        updateCheckVote(request.checkVote());
    }

    private void updateCheckVote(Boolean checkVote) {
        if (checkVote != null && this.checkVote != checkVote) {
            this.checkVote = checkVote;
        }
    }

    public void finishVote() {
        this.isVote = true;
    }

    public void recommend(GrahamUtils.Point point) {
        this.recommendedLocationX = point.getX();
        this.recommendedLocationY = point.getY();
    }
}
