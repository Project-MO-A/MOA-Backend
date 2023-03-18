package com.moa.domain.recruit.category;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class RecruitCategoryId implements Serializable {
    @Column(name = "RECRUIMENT_ID")
    private Long recruitId;

    @Column(name = "CATEGORY_ID")
    private Long categoryId;

    public RecruitCategoryId(Long recruitId, Long categoryId) {
        this.recruitId = recruitId;
        this.categoryId = categoryId;
    }
}
