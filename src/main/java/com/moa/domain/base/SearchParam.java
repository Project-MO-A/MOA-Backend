package com.moa.domain.base;

import com.querydsl.core.types.Order;
import lombok.Getter;

@Getter
public enum SearchParam {
    CATEGORY("category"),
    TITLE("title"),
    TAG("tag"),
    STATE_CODE("stateCode"),

    NAME("name"),
    NICKNAME("nickname"),

    CREATED_DATE("createdDate"),
    MODIFIED_DATE("modifiedDate"),
    DAYS_AGO("daysAgo"),

    DESC("desc"),
    ASC("asc")
    ;

    private final String paramKey;

    SearchParam(String paramKey) {
        this.paramKey = paramKey;
    }

    public static Order getOrder(String direction) {
        if (direction.equals(DESC.name())) return Order.DESC;
        return Order.ASC;
    }
}
