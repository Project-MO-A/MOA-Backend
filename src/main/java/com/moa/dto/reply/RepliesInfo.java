package com.moa.dto.reply;

import com.moa.domain.reply.Reply;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static java.util.Locale.ENGLISH;

@Getter
public class RepliesInfo {

    private final Map<Long, ReplyInfo> info;

    public RepliesInfo(List<Reply> replys) {
        Map<Long, ReplyInfo> result = new HashMap<>();
        Map<Long, Long> parentInfo = new HashMap<>();

        for (Reply reply : replys) {
            Long id = reply.getId();
            Long parentId = reply.getParentId();
            String content = reply.getContent();
            String nickname = reply.getUser().getNickname();
            String createdDate = reply.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a").withLocale(ENGLISH));

            ReplyInfo replyInfo = new ReplyInfo(id, nickname, content, createdDate, new HashMap<>());
            if (parentId == null) {
                parentInfo.put(id, id);
                result.put(id, replyInfo);
                continue;
            }
            parentInfo.put(id, parentId);
            getSubRepliesLoop(result, parentInfo, parentId).put(id, replyInfo);
        }
        this.info = result;
    }

    private Map<Long, ReplyInfo> getSubRepliesRecursive(Map<Long, ReplyInfo> result, Map<Long, Long> parentInfo, Long parentId) {
        if (isRootReply(parentInfo, parentId)) {
            return result.get(parentId).subReplies;
        }
        return getSubRepliesRecursive(result, parentInfo, parentInfo.get(parentId)).get(parentId).subReplies;
    }

    private Map<Long, ReplyInfo> getSubRepliesLoop(Map<Long, ReplyInfo> result, Map<Long, Long> parentInfo, Long parentId) {
        Stack<Long> ids = getReplyIdDepth(parentInfo, parentId);
        Map<Long, ReplyInfo> response = result.get(ids.pop()).subReplies;
        while (!ids.isEmpty()) {
            response = response.get(ids.pop()).subReplies;
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
            Long id,
            String author,
            String content,
            String createDate,
            Map<Long, ReplyInfo> subReplies
    ) {}
}
