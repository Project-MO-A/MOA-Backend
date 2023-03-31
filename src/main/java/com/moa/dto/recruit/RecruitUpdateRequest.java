package com.moa.dto.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.dto.member.RecruitMemberRequest;
import com.moa.global.exception.service.InvalidRequestException;
import lombok.Builder;

import java.util.List;

import static com.moa.global.exception.ErrorCode.REQUEST_INVALID;

@Builder
public record RecruitUpdateRequest(
        String title,
        String content,
        Integer state,
        List<RecruitMemberRequest> memberFields,
        List<String> tags
) {
    public List<RecruitMember> toMemberList() {
        if (memberFields == null || memberFields.isEmpty()) throw new InvalidRequestException(REQUEST_INVALID);
        return memberFields.stream()
                .map(field -> RecruitMember.builder()
                        .recruitField(field.field())
                        .totalRecruitCount(field.total())
                        .build())
                .toList();
    }
}
