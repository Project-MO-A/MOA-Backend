package com.moa.controller;

import com.moa.dto.ValueResponse;
import com.moa.dto.notice.*;
import com.moa.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/recruitment/{recruitmentId}/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @ResponseStatus(CREATED)
    @PostMapping
    public ValueResponse<Long> postNotice(@PathVariable Long recruitmentId, @RequestBody @Valid PostNoticeRequest request){
        return new ValueResponse<>(noticeService.post(recruitmentId, request));
    }

    @PatchMapping("/{noticeId}")
    public ValueResponse<Long> update(@PathVariable Long recruitmentId, @PathVariable Long noticeId, @RequestBody @Valid UpdateNoticeRequest request){
        return new ValueResponse<>(noticeService.update(recruitmentId, noticeId, request));
    }

    @DeleteMapping("/{noticeId}")
    public ValueResponse<Long> delete(@PathVariable Long recruitmentId, @PathVariable Long noticeId){
        return new ValueResponse<>(noticeService.delete(recruitmentId, noticeId));
    }

    @GetMapping
    public NoticesResponse findAll(@PathVariable Long recruitmentId) {
        return noticeService.findAll(recruitmentId);
    }

}
