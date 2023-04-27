package com.moa.domain.user;

public enum AlarmType {
    RECRUITMENT_COMPLETE("%d번 모집글에 대한 인원 모집이 완료되었습니다.", "/recruitment/%d"),
    PARTICIPATION_REQUEST("%d번 모집글에 대한 새로운 참여 요청이 등록되었습니다.", "/recruitment/%d/users"),
    PARTICIPATION_APPROVAL("%d번 모집글에 대한 참여 요청이 승인되었습니다.", "/recruitment/%d"),
    PARTICIPATION_REJECT("%d번 모집글에 대한 참여 요청이 거절되었습니다.", "/recruitment/%d"),

    NOTICE_POST("공지사항이 등록되었습니다.", "/notice/%d"),

    TIME_POST("%d번 공지사항에 대한 시간 조율이 등록되었습니다.", "/notice/%d/time"),
    TIME_COMPLETE("%d번 공지사항에 대한 시간 조율이 완료되었습니다.", "/notice/%d/time"),

    PLACE_POST("%d번 공지사항에 대한 장소 조율이 등록되었습니다.", "/notice/%d/place"),
    PLACE_COMPLETE("%d번 공지사항에 대한 장소 조율이 완료되었습니다.", "/notice/%d/place");

    private final String message;
    private final String uri;

    AlarmType(String message, String uri) {
        this.message = message;
        this.uri = uri;
    }

    public String getRedirectURI(Long id) {
        return String.format(this.uri, id);
    }

    public String getMessage(Long id) {
        return String.format(this.message, id);
    }
}
