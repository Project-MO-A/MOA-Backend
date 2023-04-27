package com.moa.dto.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Category;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Locale.ENGLISH;

@Getter
public class RecruitmentInfo {
    protected final Long id;
    protected final String title;
    protected final String author;
    protected final String createdDate;
    protected final String recruitStatus;
    protected final String category;
    protected List<String> tags;
    protected final int totalCount;
    protected final int approvedCount;
    protected String profileImage;
    protected int replyCount;
    
    public RecruitmentInfo(Recruitment recruitment) {
        this.id = recruitment.getId();
        this.title = recruitment.getPost().getTitle();
        this.author = recruitment.getUser().getNickname();
        this.createdDate = convertToString(recruitment.getCreatedDate());
        this.recruitStatus = recruitment.getStatus().getStatus();
        this.category = recruitment.getCategory().getValue();
        this.tags = getTagNames(recruitment);
        this.totalCount = getTotal(recruitment);
        this.approvedCount = getApproved(recruitment);
        this.profileImage = "profileImage";
    }

    public RecruitmentInfo(Long id, String title, String author, LocalDateTime createdDate, RecruitStatus recruitStatus, Category category, int totalCount, int approvedCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.createdDate = convertToString(createdDate);
        this.recruitStatus = recruitStatus.getStatus();
        this.category = category.getValue();
        this.totalCount = totalCount;
        this.approvedCount = approvedCount;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setReplyCount(final int replyCount) {
        this.replyCount = replyCount;
    }

    private String convertToString(final LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a").withLocale(ENGLISH));
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