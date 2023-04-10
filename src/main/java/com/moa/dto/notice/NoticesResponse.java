package com.moa.dto.notice;

import com.moa.domain.member.AttendMember;
import com.moa.domain.notice.Notice;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.moa.domain.member.Attendance.*;

public record NoticesResponse(Map<Long, NoticeResponse> notices) {
    public NoticesResponse(List<Notice> notices, List<AttendMember> attendMembers) {
        this(createResponse(notices, attendMembers));
    }

    private static Map<Long, NoticeResponse> createResponse(List<Notice> notices, List<AttendMember> attendMembers) {
        Map<Long, Notice> noticeMap = listToMap(notices);

        Map<Long, NoticeResponse> response = new HashMap<>();
        for (AttendMember attendMember : attendMembers) {
            Long noticeId = attendMember.getNotice().getId();
            NoticeResponse noticeResponse = response.getOrDefault(noticeId, new NoticeResponse(noticeMap.get(noticeId)));
            noticeResponse.addMember(attendMember.getAttendance().name(), attendMember);
            response.put(noticeId, noticeResponse);
        }
        return response;
    }

    private static Map<Long, Notice> listToMap(List<Notice> notices) {
        return notices.stream()
                .collect(Collectors.toMap(
                        Notice::getId,
                        notice -> notice));
    }

    public record NoticeResponse(
            String content,
            String createdAt,
            Map<String, List<String>> members
    ) {
        public NoticeResponse(Notice notice) {
            this(notice.getPost().getContent(), notice.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")),attendanceMap());
        }

        public void addMember(String attendance, AttendMember attendMember) {
            List<String> members = this.members.get(attendance);
            members.add(attendMember.getUser().getNickname() == null ? attendMember.getUser().getName() : attendMember.getUser().getNickname());
            this.members.put(attendance, members);
        }
    }
}
