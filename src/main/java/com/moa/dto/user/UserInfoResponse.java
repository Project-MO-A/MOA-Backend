package com.moa.dto.user;

import com.moa.domain.interests.Interests;
import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.RecruitMember;
import com.moa.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Comparator.*;

@Getter
public class UserInfoResponse {
    private final UserInfo userInfo;
    private final List<RecruitmentInfo> recruitPostInfo;

    public UserInfoResponse(List<ApplimentMember> applimentMembers) {
        if (applimentMembers.size() == 0) {
            this.userInfo = UserInfo.builder().build();
            this.recruitPostInfo = Collections.emptyList();
        } else {
            this.userInfo = setUserInfo(applimentMembers);
            this.recruitPostInfo = setRecruitPostInfo(applimentMembers);
        }
    }

    private UserInfo setUserInfo(List<ApplimentMember> applimentMembers) {
        User user = applimentMembers.get(0).getUser();
        List<String> interests = user.getInterests().stream()
                .map(Interests::getName)
                .toList();

        return UserInfo.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .locationLatitude(user.getLocationLatitude())
                .locationLongitude(user.getLocationLongitude())
                .popularity(user.getPopularity())
                .details(user.getDetails())
                .interests(interests)
                .build();
    }

    private List<RecruitmentInfo> setRecruitPostInfo(List<ApplimentMember> applimentMembers) {
        sortDtoByRecruitMemberId(applimentMembers);

        List<RecruitmentInfo> infos = new ArrayList<>();
        for (ApplimentMember applimentMember : applimentMembers) {
            RecruitMember recruitMember = applimentMember.getRecruitMember();
            infos.add(RecruitmentInfo.builder()
                    .id(recruitMember.getId())
                    .title(recruitMember.getRecruitment().getPost().getTitle())
                    .postState(recruitMember.getRecruitment().getStatus().name())
                    .recruitState(applimentMember.getApproval().name())
                    .build());
        }
        return infos;
    }

    private static void sortDtoByRecruitMemberId(List<ApplimentMember> all) {
        all.sort(comparing(o -> o.getRecruitMember().getId()));
    }

    @Builder
    public record RecruitmentInfo(
            Long id,
            String title,
            String postState,
            String recruitState
    ) {
    }

    @Builder
    public record UserInfo(
            String email,
            String name,
            String nickname,
            double locationLatitude,
            double locationLongitude,
            int popularity,
            String details,
            List<String> interests
    ) {
    }
}
