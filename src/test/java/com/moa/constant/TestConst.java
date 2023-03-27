package com.moa.constant;

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
}
