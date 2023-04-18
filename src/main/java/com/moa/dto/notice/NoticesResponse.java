package com.moa.dto.notice;

import com.moa.domain.member.AttendMember;
import com.moa.domain.notice.Notice;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.moa.domain.member.Attendance.*;

public record NoticesResponse(List<NoticeResponse> notices) {
    public NoticesResponse(List<Notice> notices, List<AttendMember> attendMembers) {
        this(createResponse(notices, attendMembers));
    }

    private static List<NoticeResponse> createResponse(List<Notice> notices, List<AttendMember> attendMembers) {
        Map<Long, Notice> noticeMap = listToMap(notices);

        Map<Long, NoticeResponse> response = new HashMap<>();
        for (AttendMember attendMember : attendMembers) {
            Long noticeId = attendMember.getNotice().getId();
            NoticeResponse noticeResponse = response.getOrDefault(noticeId, new NoticeResponse(noticeMap.get(noticeId)));
            noticeResponse.addMember(attendMember.getAttendance().name(), attendMember);
            response.put(noticeId, noticeResponse);
        }

        return response.values().stream().toList();
    }

    private static Map<Long, Notice> listToMap(List<Notice> notices) {
        return notices.stream()
                .collect(Collectors.toMap(
                        Notice::getId,
                        notice -> notice));
    }

    @Getter
    public static class NoticeResponse {
        private final Long noticeId;
        private final String content;
        private final String createdAt;
        private final Map<String, List<String>> members;

        public NoticeResponse(Notice notice) {
            this(notice.getId(), notice.getPost().getContent(), notice.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")), attendanceMap());
        }

        public NoticeResponse(Long noticeId, String content, String createdAt, Map<String, List<String>> members) {
            this.noticeId = noticeId;
            this.content = content;
            this.createdAt = createdAt;
            this.members = members;
        }

        public void addMember(String attendance, AttendMember attendMember) {
            List<String> members = this.members.get(attendance);
            members.add(attendMember.getUser().getNickname() == null ? attendMember.getUser().getName() : attendMember.getUser().getNickname());
            this.members.put(attendance, members);
        }
    }
}
