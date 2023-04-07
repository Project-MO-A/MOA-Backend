package com.moa.service;

import com.moa.domain.interests.RecruitmentInterest;
import com.moa.domain.interests.RecruitmentInterestsRepository;
import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.recruit.tag.RecruitTag;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.StatusResponse;
import com.moa.dto.member.RecruitMemberRequest;
import com.moa.dto.recruit.RecruitInfoResponse;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.recruit.RecruitUpdateRequest;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.global.exception.service.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.moa.domain.member.ApprovalStatus.APPROVED;
import static com.moa.global.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
public class RecruitmentService {
    private final RecruitmentInterestsRepository recruitmentInterestsRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;

    public Long post(final Long userId, final RecruitPostRequest request, final List<Tag> tags) {
        User user = userRepository.getReferenceById(userId);
        Recruitment recruitment = request.toEntity(user, request.toMemberList(), getRecruitTags(tags));
        addLeader(user, recruitment);
        return recruitmentRepository.save(recruitment).getId();
    }

    @Transactional(readOnly = true)
    public RecruitInfoResponse getInfo(final Long recruitId) {
        Recruitment recruitment = recruitmentRepository.findByIdFetchUser(recruitId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
        return new RecruitInfoResponse(recruitment);
    }

    public Long update(final Long recruitId, final RecruitUpdateRequest request, final List<Tag> tags) {
        Recruitment recruitment = recruitmentRepository.findByIdFetchMembers(recruitId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
        recruitment.update(request, getRecruitTags(tags));
        updateRecruitMember(request, recruitment);

        return recruitment.getId();
    }

    public StatusResponse updateStatus(final Long recruitId, final Integer statusCode) {
        Recruitment recruitment = recruitmentRepository.findById(recruitId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
        recruitment.updateState(statusCode);
        return new StatusResponse(recruitment.getStatus().name());
    }

    public Long delete(final Long recruitId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
        recruitmentRepository.delete(recruitment);
        return recruitId;
    }

    public Long concern(Long recruitmentId, Long userId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new EntityNotFoundException(RECRUITMENT_NOT_FOUND));
        User user = userRepository.getReferenceById(userId);
        return recruitmentInterestsRepository.save(new RecruitmentInterest(user, recruitment)).getId();
    }

    private void updateRecruitMember(RecruitUpdateRequest request, Recruitment recruitment) {
        if (request.memberFields().isEmpty()) throw new InvalidRequestException(REQUEST_INVALID);

        List<RecruitMember> exists = new ArrayList<>(recruitment.getMembers());
        List<RecruitMemberRequest> memberRequests = request.memberFields();
        saveNewOrUpdateMember(recruitment, exists, memberRequests);
        deleteMember(recruitment, exists);
    }

    private void saveNewOrUpdateMember(Recruitment recruitment, List<RecruitMember> exists, List<RecruitMemberRequest> memberRequests) {
        Map<Long, Optional<RecruitMember>> recruitmentMap = new ConcurrentHashMap<>();
        for (RecruitMember member : exists) {
            recruitmentMap.put(member.getId(), Optional.of(member));
        }

        for (RecruitMemberRequest memberRequest : memberRequests) {
            Long id = memberRequest.recruitMemberId();

            if (id == null || id == 0) {
                saveNewMember(recruitment, memberRequest);
            } else {
                RecruitMember recruitMember = recruitmentMap.getOrDefault(id, Optional.empty())
                        .orElseThrow(() -> new EntityNotFoundException(RECRUITMEMBER_NOT_FOUND));
                recruitMember.update(memberRequest);
                exists.remove(recruitMember);
            }
        }
    }

    private void saveNewMember(Recruitment recruitment, RecruitMemberRequest memberRequest) {
        RecruitMember recruitMember = RecruitMember.builder()
                .recruitment(recruitment)
                .recruitField(memberRequest.field())
                .totalRecruitCount(memberRequest.total())
                .build();
        recruitment.setMember(recruitMember);
    }

    private void deleteMember(Recruitment recruitment, List<RecruitMember> members) {
        List<RecruitMember> exists = members.stream()
                .filter(m -> m.getCurrentRecruitCount() == 0)
                .toList();
        recruitment.getMembers().removeAll(exists);
    }

    private List<RecruitTag> getRecruitTags(List<Tag> tags) {
        return tags.stream()
                .map(RecruitTag::new)
                .toList();
    }

    private void addLeader(User user, Recruitment recruitment) {
        RecruitMember leaderMember = new RecruitMember(recruitment);
        leaderMember.addApplimentMember(new ApplimentMember(leaderMember, user, APPROVED));
        recruitment.setMember(leaderMember);
    }


}
