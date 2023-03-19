package com.moa.dto.recruit;

import com.moa.domain.member.RecruitMember;
import com.moa.dto.member.RecruitMemberRequest;
import lombok.Builder;

import java.util.List;

@Builder
public record RecruitUpdateRequest(
        String title,
        String content,
        Integer state,
        List<RecruitMemberRequest> memberFields,
        List<String> category
) {
    public List<RecruitMember> toMemberList() {
        if (memberFields == null || memberFields.isEmpty()) return null;
        return memberFields.stream()
                .map(field -> new RecruitMember(field.field(), field.total()))
                .toList();
    }
}
