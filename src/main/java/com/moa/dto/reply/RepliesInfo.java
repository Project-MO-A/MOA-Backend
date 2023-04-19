package com.moa.dto.reply;

import com.moa.domain.reply.Reply;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Locale.ENGLISH;

@Getter
public class RepliesInfo {

    private final List<ReplyInfo> info;

    public RepliesInfo(List<Reply> replys) {
        Map<Long, ReplyInfo> result = new HashMap<>();
        Map<Long, Long> parentInfo = new HashMap<>();

        for (Reply reply : replys) {
            Long id = reply.getId();
            Long parentId = reply.getParentId();
            String content = reply.getContent();
            String nickname = reply.getUser().getNickname();
            Long userId = reply.getUser().getId();
            String createdDate = reply.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a").withLocale(ENGLISH));

            ReplyInfo replyInfo = new ReplyInfo(id, userId, nickname, content, createdDate, new ArrayList<>());
            if (parentId == null) {
                parentInfo.put(id, id);
                result.put(id, replyInfo);
                continue;
            }
            parentInfo.put(id, parentId);
            getSubRepliesLoop(result, parentInfo, parentId).add(replyInfo);
        }
        this.info = result.values().stream().toList();
    }

    private List<ReplyInfo> getSubRepliesLoop(Map<Long, ReplyInfo> result, Map<Long, Long> parentInfo, Long parentId) {
        Stack<Long> ids = getReplyIdDepth(parentInfo, parentId);
        List<ReplyInfo> response = result.get(ids.pop()).subReplies;
        while (!ids.isEmpty()) {
            for (ReplyInfo replyInfo : response) {
                if (replyInfo.replyId.equals(ids.pop())) {
                    response = replyInfo.subReplies;
                    break;
                }
            }
        }
        return response;
    }

    private static Stack<Long> getReplyIdDepth(Map<Long, Long> parentInfo, Long parentId) {
        Stack<Long> stack = new Stack<>();
        stack.push(parentId);
        while (!isRootReply(parentInfo, stack.peek())) {
            stack.push(parentInfo.get(stack.peek()));
        }
        return stack;
    }

    private static boolean isRootReply(Map<Long, Long> parentInfo, Long parentId) {
        return parentInfo.get(parentId).equals(parentId);
    }

    record ReplyInfo(
            Long replyId,
            Long userId,
            String author,
            String content,
            String createdDate,
            List<ReplyInfo> subReplies
    ) {}
}
