package com.moa.domain.user;

public enum AlarmType {
    RECRUITMENT_COMPLETE("{id}번 모집글에 대한 인원 모집이 완료되었습니다.", "/recruitment/{id}"),
    PARTICIPATION_REQUEST("{id}번 모집글에 대한 새로운 참여 요청이 등록되었습니다.", "/recruitment/{id}/users"),
    PARTICIPATION_APPROVAL("{id}번 모집글에 대한 참여 요청이 승인되었습니다.", "/recruitment/{id}"),
    PARTICIPATION_REJECT("{id}번 모집글에 대한 참여 요청이 거절되었습니다.", "/recruitment/{id}"),

    NOTICE_POST("공지사항이 등록되었습니다.", "/notice/{id}"),

    TIME_POST("{id}번 공지사항에 대한 시간 조율이 등록되었습니다.", "/notice/{id}/time"),
    TIME_COMPLETE("{id}번 공지사항에 대한 시간 조율이 완료되었습니다.", "/notice/{id}/time"),

    PLACE_POST("{id}번 공지사항에 대한 장소 조율이 등록되었습니다.", "/notice/{id}/place"),
    PLACE_COMPLETE("{id}번 공지사항에 대한 장소 조율이 완료되었습니다.", "/notice/{id}/place");

    private final String message;
    private final String uri;

    AlarmType(String message, String uri) {
        this.message = message;
        this.uri = uri;
    }

    public String getRedirectURI(Long id) {
        return this.uri.replaceFirst("\\{id}", String.valueOf(id));
    }

    public String getMessage(Long id) {
        return this.message.replaceFirst("\\{id}", String.valueOf(id));
    }
}
