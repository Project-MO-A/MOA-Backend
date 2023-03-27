package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.member.ApplimentSearchRepository;
import com.moa.domain.possible.Day;
import com.moa.domain.possible.PossibleTime;
import com.moa.domain.possible.PossibleTimeRepository;
import com.moa.dto.member.ApprovedMemberResponse;
import com.moa.dto.possible.PossibleTimeRequest;
import com.moa.dto.possible.PossibleTimeResponse;
import com.moa.global.exception.service.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.moa.global.exception.ErrorCode.APPLIMENT_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class PossibleTimeService {
    private final ApplimentSearchRepository applimentSearchRepository;
    private final PossibleTimeRepository possibleTimeRepository;
    private final ApplimentMemberRepository applimentMemberRepository;

    @Transactional(readOnly = true)
    public List<PossibleTimeResponse> getAllMembersTimeList(final Long recruitmentId) {
        List<ApprovedMemberResponse> allMembers = applimentSearchRepository.findAllApprovedMembers(recruitmentId);
        return allMembers.stream().map(all -> PossibleTimeResponse.builder()
                    .nickname(all.getNickname())
                    .possibleTimes(possibleTimeRepository.findAllByApplimentMemberId(all.getApplyId())).build())
                .toList();
    }

    @Transactional(readOnly = true)
    public PossibleTimeResponse getTimeList(final Long recruitmentId, final Long userId) {
        ApplimentMember applimentMember = applimentMemberRepository.findByRecruitIdAndUserId(recruitmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));
        List<PossibleTime> allByApplyId = possibleTimeRepository.findAllByApplimentMemberId(applimentMember.getId());
        return new PossibleTimeResponse("success", allByApplyId);
    }

    public void setTime(final PossibleTimeRequest timeRequestList, final Long recruitmentId, final Long userId) {
        ApplimentMember applimentMember = applimentMemberRepository.findByRecruitIdAndUserId(recruitmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));
        possibleTimeRepository.deleteAllByApplimentMember(applimentMember);

        timeRequestList.possibleTimeDataList().stream()
                .map(time -> PossibleTime.builder()
                        .applimentMember(applimentMember)
                        .day(Day.valueOf(time.day()))
                        .startTime(time.startTime())
                        .endTime(time.endTime())
                        .build())
                .forEach(possibleTimeRepository::save);
    }
}
