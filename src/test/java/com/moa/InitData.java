package com.moa;

import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
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

import java.util.ArrayList;
import java.util.List;

@Profile("data")
@Component
@RequiredArgsConstructor
public class InitData {
    private final Initialize initialize;

    public static User USER1;
    public static User USER2;
    public static User USER3;
    public static Recruitment RECRUITMENT1;

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

            UserSignupRequest userRequest2 = UserSignupRequest
                    .builder()
                    .email("test2@email.com")
                    .password("password")
                    .name("name2")
                    .nickname("nickname2")
                    .details("details")
                    .locationLatitude(34.1234124)
                    .locationLongitude(22.1234124)
                    .interests(interest)
                    .build();

            UserSignupRequest userRequest3 = UserSignupRequest
                    .builder()
                    .email("test3@email.com")
                    .password("password")
                    .name("name3")
                    .nickname("nickname3")
                    .details("details")
                    .locationLatitude(34.1234124)
                    .locationLongitude(22.1234124)
                    .interests(interest)
                    .build();

            userService.saveUser(userRequest3);
            USER3 = userRepository.findByEmail("test3@email.com").get();

            userService.saveUser(userRequest);
            USER1 = userRepository.findByEmail("test@email.com").get();

            userService.saveUser(userRequest2);
            USER2 = userRepository.findByEmail("test2@email.com").get();

            RecruitPostRequest request = RecruitPostRequest
                    .builder()
                    .title("title")
                    .content("content")
                    .memberFields(List.of(new RecruitMemberRequest("백엔드", 5),
                            new RecruitMemberRequest("프론트엔드", 5)
                    ))
                    .tags(List.of("프로젝트", "웹", "Java", "MySQL"))
                    .build();
            List<Long> categoryId = tagService.updateAndReturnId(request.tags()).orElse(new ArrayList<>());
            Long recruitId1 = recruitmentService.post(USER1.getId(), request, categoryId);
            RECRUITMENT1 = recruitmentRepository.findById(recruitId1).get();
        }
    }
}
