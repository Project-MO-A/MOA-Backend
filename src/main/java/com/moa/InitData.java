package com.moa;

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
            recruitmentService.post(user.getId(), request, categoryId);
        }
    }
}
