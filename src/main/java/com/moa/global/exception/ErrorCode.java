package com.moa.global.exception;

public enum ErrorCode {
    BAD_HTTP_REQUEST(400, "bad.request", "A0002"),
    JWT_NOT_VALID(400, "token.not.valid", "A0003"),
    LOGIN_BAD_VALUE(401, "login.bad.value", "A0004"),

    STATUS_CODE_INVALID(404, "status.code.invalid", "S0001"),
    STATUS_CODE_REPLACE_TO_KICK(404, "status.code.replace.to.kick", "S0002"),

    USER_NOT_FOUND(404, "user.not.found", "U0001"),
    USER_DUPLICATED_EMAIL(400, "user.duplicate.email", "U0002"),
    USER_MISMATCH_PASSWORD(400, "user.mismatch.password", "U0003"),

    NOTICE_NOT_FOUND(404, "notice.not.found", "N0001"),
    NOTICE_ASSOCIATION_MISMATCH(404, "notice.association.mismatch", "N0002"),
    NOTICE_VOTE_FINISH(400, "notice.vote.finish", "N0003"),
    CAN_NOT_STOP_VOTE(400, "can.not.stop.vote", "N0004"),

    ATTENDMEMBER_NOT_FOUND(400, "attendmember.not.found", "AM0001"),
    
    RECRUITMENT_NOT_FOUND(404, "recruitment.not.found", "R0001"),

    RECRUITMEMEBER_NO_FIELD(404, "recruitmemeber.no.field", "R0002"),
    RECRUITMEMBER_FULL_COUNT(400, "recruitmemeber.full.count", "R0003"),
    RECRUITMEMBER_ZERO_COUNT(400, "recruitmemeber.zero.count", "R0004"),
    RECRUITMEMBER_NOT_FOUND(404, "recruitmember.not.found", "R0005"),
    RECRUITMEMBER_NO_FIELD_OR_RECRUIT_NOT_FOUND(404, "recruitmemeber.no.field.or.recruitment.not.found", "R006"),

    APPLIMENT_NOT_FOUND(404, "appliment.not.found", "P0001"),
    APPLIMENT_STATUS_CHANGE_LEADER(400, "appliment.status.change.leader", "P0002"),
    APPLIMENT_DUPLICATE(400, "appliment.duplicate", "P0003"),

    CATEGORY_NOT_FOUND(404, "category.not.found", "CA001"),

    TIME_INVALID(400, "time.invalid", "T0001"),
    REQUEST_INVALID(400, "request.invalid", "RQ001"),
    COUNT_INVALID(400, "count.invalid", "C0001"),
    NUMBER_FORMAT(400, "number.format", "NU001"),

    REPLY_NOT_FOUND(400, "reply.not.found", "RP001"),
    REPLY_AUTHORITY(401, "reply.authority", "RP002"),

    IMAGE_NOT_PROCESS(400, "image.not.process", "I0001"),
    IO_ERROR(400, "io.error", "IO0001")
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
