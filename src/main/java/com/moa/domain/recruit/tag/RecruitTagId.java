package com.moa.domain.recruit.tag;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class RecruitTagId implements Serializable {
    @Column(name = "RECRUIMENT_ID")
    private Long recruitId;

    @Column(name = "TAG_ID")
    private Long tagId;

    public RecruitTagId(Long recruitId, Long tagId) {
        this.recruitId = recruitId;
        this.tagId = tagId;
    }
}
