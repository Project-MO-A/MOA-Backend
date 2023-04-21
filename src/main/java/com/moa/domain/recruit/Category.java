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
    INTERVIEW("면접"),
    AUTONOMY("자율"),
    ETC("기타")
    ;

    private final String value;
    Category(String value) {
        this.value = value;
    }

    public static Category getInstanceByValue(String value) {
        return Arrays.stream(Category.values())
                .filter(category -> category.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
    }

    public static Category getInstanceByName(String name) {
        return Arrays.stream(Category.values())
                .filter(category -> category.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
    }
}
