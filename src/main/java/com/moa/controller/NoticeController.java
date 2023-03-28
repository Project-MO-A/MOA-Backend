package com.moa.controller;

import com.moa.dto.IdResponse;
import com.moa.dto.notice.*;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.AttendMemberService;
import com.moa.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/recruitment/{recruitmentId}/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final AttendMemberService attendMemberService;

    @ResponseStatus(CREATED)
    @PostMapping
    public IdResponse postNotice(@PathVariable Long recruitmentId, @RequestBody @Valid PostNoticeRequest request){
        return new IdResponse(noticeService.post(recruitmentId, request));
    }

    @PatchMapping("/{noticeId}")
    public IdResponse update(@PathVariable Long recruitmentId, @PathVariable Long noticeId, @RequestBody @Valid UpdateNoticeRequest request){
        return new IdResponse(noticeService.update(recruitmentId, noticeId, request));
    }

    @DeleteMapping("/{noticeId}")
    public IdResponse delete(@PathVariable Long recruitmentId, @PathVariable Long noticeId){
        return new IdResponse(noticeService.delete(noticeId, recruitmentId));
    }

    @GetMapping
    public NoticesResponse findAll(@PathVariable Long recruitmentId) {
        return noticeService.findAll(recruitmentId);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/{noticeId}/vote/{attendance}")
    public IdResponse voteAttendance(@PathVariable Long recruitmentId, @PathVariable Long noticeId, @PathVariable String attendance, @AuthenticationPrincipal JwtUser user){
        VoteAttendanceRequest request = VoteAttendanceRequest.builder()
                .recruitmentId(recruitmentId)
                .noticeId(noticeId)
                .attendance(attendance)
                .userId(user.id())
                .build();
        return new IdResponse(attendMemberService.voteAttendance(request));
    }
}
