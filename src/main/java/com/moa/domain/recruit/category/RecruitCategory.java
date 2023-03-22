package com.moa.domain.recruit.category;

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
public class RecruitCategory extends BaseTimeEntity implements Persistable<RecruitCategoryId> {
    @EmbeddedId
    private RecruitCategoryId id;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @MapsId("recruitId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUIMENT_ID")
    private Recruitment recruitment;

    public RecruitCategory(Category c) {
        this.category = c;
    }

    public void setParent(Recruitment recruitment) {
        this.recruitment = recruitment;
        this.id = new RecruitCategoryId(recruitment.getId(), category.getId());
    }

    @Override
    public RecruitCategoryId getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
