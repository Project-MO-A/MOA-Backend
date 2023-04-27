package com.moa.dto.recruit;

import com.moa.domain.recruit.Recruitment;
import lombok.Getter;

@Getter
public class RecruitmentInterestInfo extends RecruitmentInfo {
    private final String redirectUri;

    public RecruitmentInterestInfo(Recruitment recruitment, String redirectUri) {
        super(recruitment);
        this.redirectUri = redirectUri;
    }
}
