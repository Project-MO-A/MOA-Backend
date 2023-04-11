package com.moa.service;

import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.reply.ReplyRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.reply.ReplyPostRequest;
import com.moa.global.exception.ErrorCode;
import com.moa.global.exception.service.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private void validParentId(ReplyPostRequest request) {
        if (request.parentId() != null) {
            replyRepository.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REPLY_NOT_FOUND));
        }
    }
}
