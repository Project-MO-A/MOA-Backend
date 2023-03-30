package com.moa.support.fixture;

import com.moa.domain.interests.Interests;
import com.moa.domain.user.Popularity;
import com.moa.domain.user.User;

import java.util.ArrayList;
import java.util.List;

public enum UserFixture {
    KAI("kai123@email.com", "asdfsqwe1234!", "이기우", "kai",
            34.123124, 31.123451, "안녕하세욤"),
    EUNSEO("eunseo123@email.com", "asdfsqwe1234!", "조은서", "eunseo",
            34.123124, 31.123451, "잘 부탁드려요"),
    JHS("jhs123@email.com", "asdfsqwe1234!", "주홍석", "jhs",
            34.123124, 31.123451, "개발하고싶어요"),
    PINGU("pingu123@email.com", "asdfsqwe1234!", "박신비", "pingu",
            34.123124, 31.123451, "핑구 좋아하세요?"),
    LION("lion123@email.com", "asdfsqwe1234!", "김사자", "lion",
                  34.123124, 31.123451, "사장"),
    TIGER("tiger@email.com", "asdfsqwe1234!", "박호랑", "tiger",
            34.123124, 31.123451, "호랑");

    private final String email;
    private final String password;
    private final String name;
    private final String nickname;
    private final double locationLatitude;
    private final double locationLongitude;
    private final String details;
    private List<Interests> interests = new ArrayList<>();

    UserFixture(final String email, final String password, final String name, final String nickname,
                final double locationLatitude, final double locationLongitude,
                final String details) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.details = details;
    }

    public User 생성() {
        return 기본_빌더_생성()
                .build();
    }

    public User 생성(final List<String> interests) {
        List<Interests> interestsList = interests.stream()
                .map(Interests::new)
                .toList();
        User user = 생성();
        user.addInterests(interestsList);
        return user;
    }

    private User.UserBuilder 기본_빌더_생성() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .nickname(this.nickname)
                .locationLatitude(this.locationLatitude)
                .locationLongitude(this.locationLongitude)
                .details(this.details);
    }

    public User 인기도를_입력하여_생성(final Popularity popularity) {
        return 기본_빌더_생성()
                    .popularity(popularity)
                    .build();
    }
}
