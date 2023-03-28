package com.moa.constant;

import com.moa.domain.interests.RecruitmentInterest;
import com.moa.domain.notice.Post;
import com.moa.domain.recruit.RecruitStatus;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;

public class TestConst {
    public static final String EMAIL = "user@email.com";
    public static final String INCORRECT_EMAIL = "Invalid@email.com";
    public static final User USER = User.builder()
            .email("user@email.com")
            .password("password")
            .name("name")
            .nickname("nickname")
            .locationLatitude(23.23)
            .locationLongitude(34.3443)
            .details("details")
            .build();

    public static final User ASSOCIATION_USER;

    static {
        ASSOCIATION_USER = User.builder()
                .email("user@email.com")
                .password("password")
                .name("name")
                .nickname("nickname")
                .locationLatitude(23.23)
                .locationLongitude(34.3443)
                .popularity(0)
                .details("details")
                .build();
        ASSOCIATION_USER
                .addRecruitmentInterests(new RecruitmentInterest(ASSOCIATION_USER, new Recruitment(USER,
                        new Post("title", "content"), RecruitStatus.RECRUITING))
                );
    }
}
