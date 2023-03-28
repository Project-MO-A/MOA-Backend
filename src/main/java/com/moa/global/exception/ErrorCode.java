package com.moa.global.exception;

public enum ErrorCode {
    BAD_HTTP_REQUEST(400, "bad.request", "A0002"),
    JWT_NOT_VALID(400, "token.not.valid", "A0003"),
    LOGIN_BAD_VALUE(401, "login.bad.value", "A0004"),

    STATUS_CODE_INVALID(400, "status.code.invalid", "S0001"),

    USER_NOT_FOUND(404, "user.not.found", "U0001"),
    USER_DUPLICATED_EMAIL(400, "user.duplicate.email", "U0002"),
    USER_MISMATCH_PASSWORD(400, "user.mismatch.password", "U0003"),

    RECRUITMENT_NOT_FOUND(404, "recruitment.not.found", "R0001"),

    APPLIMENT_NOT_FOUND(404, "appliment.not.found", "P0001"),

    TIME_INVALID(400, "time.invalid", "T0001");
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
