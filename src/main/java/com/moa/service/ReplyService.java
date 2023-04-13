package com.moa.service;

import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.reply.Reply;
import com.moa.domain.reply.ReplyRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.recruit.RecruitUpdateRequest;
import com.moa.dto.reply.RepliesInfo;
import com.moa.dto.reply.ReplyPostRequest;
import com.moa.dto.reply.ReplyUpdateRequest;
import com.moa.global.auth.model.JwtUser;
import com.moa.global.exception.ErrorCode;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.global.exception.service.ReplyAuthorityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.moa.global.exception.ErrorCode.REPLY_AUTHORITY;
import static com.moa.global.exception.ErrorCode.REPLY_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final UserRepository userRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final ReplyRepository replyRepository;

    public Long createReply(ReplyPostRequest request) {
        validParentId(request);
        User user = userRepository.getReferenceById(request.userId());
        Recruitment recruitment = recruitmentRepository.getReferenceById(request.recruitmentId());
        return replyRepository.save(request.toEntity(user, recruitment)).getId();
    }

    public RepliesInfo getAllReply(Long recruitmentId) {
        return new RepliesInfo(replyRepository.findByRecruitmentIdOrderByParentIdAsc(recruitmentId));
    }

    public Long updateReply(final Long replyId, final ReplyUpdateRequest updateRequest, final JwtUser user) {
        Reply reply = replyRepository.findFetchUserById(replyId)
                .orElseThrow(() -> new EntityNotFoundException(REPLY_NOT_FOUND));
        validAuthorization(user.id(), reply);

        reply.update(updateRequest.content());
        return replyId;
    }

    public Long deleteReply(final Long replyId, final JwtUser user) {
        Reply reply = replyRepository.findFetchUserById(replyId)
                .orElseThrow(() -> new EntityNotFoundException(REPLY_NOT_FOUND));
        validAuthorization(user.id(), reply);

        replyRepository.delete(reply);
        return replyId;
    }

    private void validAuthorization(final Long userId, final Reply reply) {
        Long replyUserId = reply.getUser().getId();
        if (!userId.equals(replyUserId)) {
            throw new ReplyAuthorityException(REPLY_AUTHORITY);
        }
    }

    private void validParentId(ReplyPostRequest request) {
        if (request.parentId() != null) {
            replyRepository.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REPLY_NOT_FOUND));
        }
    }
}
