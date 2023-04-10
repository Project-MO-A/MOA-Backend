package com.moa.dto.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RecruitmentInfo {
    protected final Long id;
    protected final String title;
    protected final String author;
    protected final LocalDateTime createdDate;
    protected final String recruitStatus;
    protected final String category;
    protected final List<String> tags;
    protected final int totalCount;
    protected final int approvedCount;
    protected String profileImage;
    protected final int replyCount;
    
    public RecruitmentInfo(Recruitment recruitment) {
        this.id = recruitment.getId();
        this.title = recruitment.getPost().getTitle();
        this.author = recruitment.getUser().getNickname();
        this.createdDate = recruitment.getCreatedDate();
        this.recruitStatus = recruitment.getStatus().getStatus();
        this.category = recruitment.getCategory().getName();
        this.tags = getTagNames(recruitment);
        this.totalCount = getTotal(recruitment);
        this.approvedCount = getApproved(recruitment);
        this.profileImage = "profileImage";

        // TO DO
        this.replyCount = 0;
    }

    private List<String> getTagNames(Recruitment recruitment) {
        return recruitment.getTags().stream()
                .map(recruitTag -> recruitTag.getTag().getName())
                .toList();
    }

    private int getTotal(Recruitment recruitment) {
        int count = 0;

        for (RecruitMember recruitMember : recruitment.getMembers()) {
            count += recruitMember.getTotalRecruitCount();
        }
        return count;
    }

    private int getApproved(Recruitment recruitment) {
        int count = 0;

        for (RecruitMember recruitMember : recruitment.getMembers()) {
            count += recruitMember.getCurrentRecruitCount();
        }
        return count;
    }
}