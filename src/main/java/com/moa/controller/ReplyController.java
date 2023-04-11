package com.moa.controller;

import com.moa.dto.ValueResponse;
import com.moa.dto.reply.ReplyPostRequest;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/recruitment/{recruitmentId}/reply")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @ResponseStatus(CREATED)
    @PostMapping
    public ValueResponse<Long> createReply(@PathVariable Long recruitmentId, @RequestParam(name = "parent", required = false) Long parentId,
            @RequestParam(name = "content") String content, @AuthenticationPrincipal JwtUser user) {
        ReplyPostRequest request = ReplyPostRequest.builder()
                .recruitmentId(recruitmentId)
                .parentId(parentId)
                .userId(user.id())
                .content(content)
                .build();

        return new ValueResponse<>(replyService.createReply(request));
    }
}
