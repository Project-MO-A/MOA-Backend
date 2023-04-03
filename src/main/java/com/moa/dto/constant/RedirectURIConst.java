package com.moa.dto.constant;

public enum RedirectURIConst {
    USER_INFO("/user/info/profile?userId=%s"),
    RECRUIT_INFO("/recruitment/%s"),
    RECRUIT_CANCEL("/recruitment/cancel/%s");

    private final String uriTemplate;

    RedirectURIConst(String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public String of(String value) {
        return String.format(uriTemplate, value);
    }
}
