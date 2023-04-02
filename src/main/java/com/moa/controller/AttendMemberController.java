package com.moa.controller;

import com.moa.dto.ValueResponse;
import com.moa.dto.notice.VoteAttendanceRequest;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.AttendMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/recruitment/{recruitmentId}/notice")
@RequiredArgsConstructor
public class AttendMemberController {

    private final AttendMemberService attendMemberService;

    @ResponseStatus(CREATED)
    @PostMapping("/{noticeId}/vote/{attendance}")
    public ValueResponse<Long> voteAttendance(@PathVariable Long recruitmentId, @PathVariable Long noticeId, @PathVariable String attendance, @AuthenticationPrincipal JwtUser user){
        VoteAttendanceRequest request = VoteAttendanceRequest.builder()
                .recruitmentId(recruitmentId)
                .noticeId(noticeId)
                .attendance(attendance)
                .userId(user.id())
                .build();
        return new ValueResponse<>(attendMemberService.voteAttendance(request));
    }
}
