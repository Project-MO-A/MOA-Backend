package com.moa.global.exception;

public enum ErrorCode {
    BAD_HTTP_REQUEST(400, "bad.request", "A0002"),
    JWT_NOT_VALID(400, "token.not.valid", "A0003"),
    LOGIN_BAD_VALUE(401, "login.bad.value", "A0004"),

    STATUS_CODE_INVALID(404, "status.code.invalid", "S0001"),

    USER_NOT_FOUND(404, "user.not.found", "U0001"),
    USER_DUPLICATED_EMAIL(400, "user.duplicate.email", "U0002"),
    USER_MISMATCH_PASSWORD(400, "user.mismatch.password", "U0003"),

    NOTICE_NOT_FOUND(404, "notice.not.found", "N0001"),
    NOTICE_ASSOCIATION_MISMATCH(404, "notice.association.mismatch", "N0002"),
    NOTICE_VOTE_FINISH(400, "notice.vote.finish", "N0003"),
    ATTENDMEMBER_NOT_FOUND(400, "attendmember.not.found", "AM0001"),
    
    RECRUITMENT_NOT_FOUND(404, "recruitment.not.found", "R0001"),

    RECRUITMEMEBER_NO_FIELD(404, "recruitmemeber.no.field", "R0002"),
    RECRUITMEMBER_FULL_COUNT(400, "recruitmemeber.full.count", "R0003"),
    RECRUITMEMBER_ZERO_COUNT(400, "recruitmemeber.zero.count", "R0004"),
    RECRUITMEMBER_NOT_FOUND(404, "recruitmember.not.found", "R0005"),

    APPLIMENT_NOT_FOUND(404, "appliment.not.found", "P0001"),

    CATEGORY_NOT_FOUND(404, "category.not.found", "CA001"),

    TIME_INVALID(400, "time.invalid", "T0001"),
    REQUEST_INVALID(400, "request.invalid", "RQ001"),
    COUNT_INVALID(400, "count.invalid", "C0001")
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
