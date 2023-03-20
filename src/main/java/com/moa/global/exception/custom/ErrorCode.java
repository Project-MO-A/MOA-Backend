package com.moa.global.exception.custom;

public enum ErrorCode {
    USER_NOT_FOUND(404, "user.not.found", "U0001"),
    DUPLICATED_EMAIL(400, "user.duplicate.email", "U0002"),
    ;

    private final int statusCode;
    private final String messageCode;
    private final String code;

    ErrorCode(int statusCode, String messageCode, String code) {
        this.statusCode = statusCode;
        this.messageCode = messageCode;
        this.code = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public String getCode() {
        return code;
    }
}
