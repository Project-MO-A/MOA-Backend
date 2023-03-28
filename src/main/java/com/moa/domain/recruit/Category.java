package com.moa.domain.recruit;

import lombok.Getter;

@Getter
public enum Category {
    LANGUAGE("어학"),
    PROGRAMMING("프로그래밍"),
    EMPLOYMENT("취업"),
    HOBBY("취미"),
    CERTIFICATE("자격증"),
    EXAMINATION("고시"),
    INTERVIEW("면접");

    private final String name;
    Category(String name) {
        this.name = name;
    }
}
