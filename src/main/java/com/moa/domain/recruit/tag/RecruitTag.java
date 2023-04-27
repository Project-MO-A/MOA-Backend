package com.moa.domain.recruit.tag;

import com.moa.domain.base.BaseTimeEntity;
import com.moa.domain.recruit.Recruitment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "RECRUIT_CATEGORY")
@Entity
public class RecruitTag extends BaseTimeEntity implements Persistable<RecruitTagId> {
    @EmbeddedId
    private RecruitTagId id;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID")
    private Tag tag;

    @MapsId("recruitId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUIMENT_ID")
    private Recruitment recruitment;

    public RecruitTag(Tag tag) {
        this.tag = tag;
    }

    public void setParent(Recruitment recruitment) {
        this.recruitment = recruitment;
        this.id = new RecruitTagId(recruitment.getId(), tag.getId());
    }

    @Override
    public RecruitTagId getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
