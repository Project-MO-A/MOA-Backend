package com.moa.domain.base;

import com.querydsl.core.types.Order;
import lombok.Getter;

@Getter
public enum SearchParam {
    CATEGORY("category"),
    TITLE("title"),
    TAG("tag"),
    NAME("name"),
    NICKNAME("nickname"),
    STATE_CODE("stateCode"),
    CREATE_DATE("createDate"),
    MODIFIED_DATE("modifiedDate"),

    DESC("desc"),
    ASC("asc")
    ;

    private final String paramKey;

    SearchParam(String paramKey) {
        this.paramKey = paramKey;
    }

    public static Order getOrder(String direction) {
        if (direction.equals(DESC.paramKey)) return Order.DESC;
        return Order.ASC;
    }
}