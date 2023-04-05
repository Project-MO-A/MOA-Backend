package com.moa;

import com.moa.domain.member.*;
import com.moa.domain.notice.NoticeRepository;
import com.moa.domain.possible.PossibleTime;
import com.moa.domain.possible.PossibleTimeRepository;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.recruit.tag.Tag;
import com.moa.domain.user.AlarmRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.member.RecruitMemberRequest;
import com.moa.dto.recruit.RecruitPostRequest;
import com.moa.dto.user.UserSignupRequest;
import com.moa.service.TagService;
import com.moa.service.RecruitmentService;
import com.moa.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.moa.domain.member.ApprovalStatus.APPROVED;
import static com.moa.domain.possible.Day.*;

@Profile("local-test")
@Component
@RequiredArgsConstructor
public class InitData {
    private final Initialize initialize;

    @PostConstruct
    public void init() {
        initialize.initUser();
    }

    @RequiredArgsConstructor
    @Component
    static class Initialize {
        private final UserService userService;
        private final UserRepository userRepository;
        private final RecruitmentService recruitmentService;
        private final RecruitmentRepository recruitmentRepository;
        private final AlarmRepository alarmRepository;
        private final NoticeRepository noticeRepository;
        private final PossibleTimeRepository possibleTimeRepository;
        private final AdminRepository adminRepository;
        private final AttendMemberRepository attendMemberRepository;
        private final RecruitMemberRepository recruitMemberRepository;
        private final ApplimentMemberRepository applimentMemberRepository;
        private final TagService tagService;

        @Transactional
        public void initUser() {
            List<String> interest = new ArrayList<>();
            interest.add("개발");
            interest.add("백엔드");
            interest.add("자바");

            UserSignupRequest userRequest = UserSignupRequest
                    .builder()
                    .email("test@email.com")
                    .password("password")
                    .name("name")
                    .nickname("nickname")
                    .details("details")
                    .locationLatitude(34.1234124)
                    .locationLongitude(22.1234124)
                    .interests(interest)
                    .build();

            userService.saveUser(userRequest);
            User user = userRepository.findByEmail("test@email.com").get();
            UserSignupRequest userRequest2 = UserSignupRequest
                    .builder()
                    .email("test2@email.com")
                    .password("password")
                    .name("name")
                    .nickname("nickname2")
                    .details("details")
                    .locationLatitude(34.1234124)
                    .locationLongitude(22.1234124)
                    .interests(interest)
                    .build();

            userService.saveUser(userRequest2);
            User user2 = userRepository.findByEmail("test2@email.com").get();
            UserSignupRequest userRequest3 = UserSignupRequest
                    .builder()
                    .email("test3@email.com")
                    .password("password")
                    .name("name")
                    .nickname("nickname3")
                    .details("details")
                    .locationLatitude(34.1234124)
                    .locationLongitude(22.1234124)
                    .interests(interest)
                    .build();

            userService.saveUser(userRequest3);
            User user3 = userRepository.findByEmail("test3@email.com").get();
            RecruitPostRequest request = RecruitPostRequest
                    .builder()
                    .title("title")
                    .content("content")
                    .memberFields(List.of(RecruitMemberRequest.builder().field("백엔드").total(5).build(),
                            RecruitMemberRequest.builder().field("프론트").total(5).build()
                    ))
                    .tags(List.of("프로젝트", "웹", "Java", "MySQL"))
                    .build();
            List<Tag> categoryId = tagService.updateAndReturn(request.tags()).orElse(new ArrayList<>());
            Long recruitmentId = recruitmentService.post(user.getId(), request, categoryId);
            Recruitment recruitment = recruitmentRepository.findById(recruitmentId).get();
            RecruitMember recruitMemberBackend = recruitMemberRepository.findByRecruitFieldAndRecruitmentId("백엔드", recruitmentId).get();
            RecruitMember recruitMemberFrontend = recruitMemberRepository.findByRecruitFieldAndRecruitmentId("프론트엔드", recruitmentId).get();

            ApplimentMember appliment1 = ApplimentMember.builder()
                    .user(user2)
                    .recruitMember(recruitMemberBackend)
                    .status(APPROVED)
                    .build();
            ApplimentMember appliment2 = ApplimentMember.builder()
                    .user(user3)
                    .recruitMember(recruitMemberFrontend)
                    .status(APPROVED)
                    .build();
            ApplimentMember applimentMember1 = applimentMemberRepository.save(appliment1);
            ApplimentMember applimentMember2 = applimentMemberRepository.save(appliment2);
            ApplimentMember leader = applimentMemberRepository.findByRecruitIdAndUserId(recruitmentId, user.getId()).get();

            PossibleTime possible1 = PossibleTime.builder()
                    .applimentMember(leader)
                    .startTime(LocalTime.of(9, 00))
                    .endTime(LocalTime.of(14, 00))
                    .day(MONDAY)
                    .build();
            PossibleTime possible2 = PossibleTime.builder()
                    .applimentMember(leader)
                    .startTime(LocalTime.of(20, 00))
                    .endTime(LocalTime.of(23, 00))
                    .day(MONDAY)
                    .build();
            PossibleTime possible3 = PossibleTime.builder()
                    .applimentMember(leader)
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(14, 00))
                    .day(TUESDAY)
                    .build();
            PossibleTime possible4 = PossibleTime.builder()
                    .applimentMember(leader)
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(23, 00))
                    .day(SATURDAY)
                    .build();
            PossibleTime possible5 = PossibleTime.builder()
                    .applimentMember(leader)
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(14, 00))
                    .day(FRIDAY)
                    .build();
            PossibleTime possible6 = PossibleTime.builder()
                    .applimentMember(leader)
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(23, 00))
                    .day(SUNDAY)
                    .build();
            possibleTimeRepository.save(possible1);
            possibleTimeRepository.save(possible2);
            possibleTimeRepository.save(possible3);
            possibleTimeRepository.save(possible4);
            possibleTimeRepository.save(possible5);
            possibleTimeRepository.save(possible6);



            PossibleTime possib1 = PossibleTime.builder()
                    .applimentMember(applimentMember1)
                    .startTime(LocalTime.of(13, 00))
                    .endTime(LocalTime.of(19, 00))
                    .day(MONDAY)
                    .build();
            PossibleTime possib2 = PossibleTime.builder()
                    .applimentMember(applimentMember1)
                    .startTime(LocalTime.of(14, 00))
                    .endTime(LocalTime.of(20, 00))
                    .day(SUNDAY)
                    .build();
            PossibleTime possib21 = PossibleTime.builder()
                    .applimentMember(applimentMember2)
                    .startTime(LocalTime.of(10, 00))
                    .endTime(LocalTime.of(23, 00))
                    .day(MONDAY)
                    .build();
            PossibleTime possib22 = PossibleTime.builder()
                    .applimentMember(applimentMember2)
                    .startTime(LocalTime.of(18, 00))
                    .endTime(LocalTime.of(22, 00))
                    .day(SUNDAY)
                    .build();
            possibleTimeRepository.save(possib1);
            possibleTimeRepository.save(possib2);
            possibleTimeRepository.save(possib21);
            possibleTimeRepository.save(possib22);
        }
    }
}
