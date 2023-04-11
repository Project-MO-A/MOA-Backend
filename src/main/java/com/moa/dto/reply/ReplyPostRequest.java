package com.moa.dto.reply;

import com.moa.domain.recruit.Recruitment;
import com.moa.domain.reply.Reply;
import com.moa.domain.user.User;
import lombok.Builder;

@Builder
public record ReplyPostRequest(String content, Long recruitmentId, Long userId, Long parentId) {

    public Reply toEntity(User user, Recruitment recruitment) {
        return new Reply(content, parentId, user, recruitment);
    }
}
