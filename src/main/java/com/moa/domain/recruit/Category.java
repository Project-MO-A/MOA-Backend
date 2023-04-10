package com.moa.domain.recruit;

import com.moa.global.exception.service.CategoryNotFoundException;
import lombok.Getter;

import java.util.Arrays;

import static com.moa.global.exception.ErrorCode.CATEGORY_NOT_FOUND;

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

    public static Category getInstance(String name) {
        return Arrays.stream(Category.values())
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
    }
}
