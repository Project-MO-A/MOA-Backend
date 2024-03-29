package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.member.ApplimentSearchRepository;
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
    public List<String> getTimeList(final Long recruitmentId, final Long userId) {
        ApplimentMember applimentMember = applimentMemberRepository.findByRecruitIdAndUserId(recruitmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));
        List<PossibleTime> possibleTimes = possibleTimeRepository.findAllByApplimentMemberId(applimentMember.getId());
        return PossibleTimeResponse.getPossibleTimeData(possibleTimes);
    }

    public void setTime(final PossibleTimeRequest timeRequestList, final Long recruitmentId, final Long userId) {
        ApplimentMember applimentMember = applimentMemberRepository.findByRecruitIdAndUserId(recruitmentId, userId)
                .orElseThrow(() -> new EntityNotFoundException(APPLIMENT_NOT_FOUND));
        possibleTimeRepository.deleteAllByApplimentMember(applimentMember);

        saveTime(timeRequestList, applimentMember);
    }

    private void saveTime(PossibleTimeRequest timeRequestList, ApplimentMember applimentMember) {
        List<PossibleTime> possibleTimeList = timeRequestList.getEntityList(applimentMember);
        possibleTimeRepository.saveAll(possibleTimeList);
    }
}
