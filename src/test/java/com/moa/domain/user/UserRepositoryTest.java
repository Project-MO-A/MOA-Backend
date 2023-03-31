package com.moa.domain.user;

import com.moa.domain.interests.RecruitmentInterest;
import com.moa.domain.recruit.Recruitment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자의 이메일로 조회한다")
    void findUserByUserEmail() {
        //given
        String userEmail = "email@naver.com";
        userRepository.save(User.builder().email(userEmail).build());

        //when
        User findUser = userRepository.findByEmail(userEmail).get();

        //then
        assertThat(findUser.getEmail()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("사용자의 refreshToken으로 조회한다")
    void findUserByUserRefreshToken() {
        //given
        User user = User.builder().name("user").build();
        user.updateRefreshToken("jwt.refresh.token");
        userRepository.save(user);

        //when
        User findUser = userRepository.findByRefreshToken("jwt.refresh.token").get();

        //then
        assertThat(findUser.getRefreshToken()).isEqualTo(user.getRefreshToken());
    }

    @Test
    @DisplayName("사용자 아이디로 관심글 목록과 함께 사용자를 조회한다")
    void findInterestsRecruitmentByUserId() {
        //given
        User user = User.builder().build();
        Recruitment recruitment1 = Recruitment.builder().build();
        Recruitment recruitment2 = Recruitment.builder().build();
        user.addRecruitmentInterests(new RecruitmentInterest(user, recruitment1));
        user.addRecruitmentInterests(new RecruitmentInterest(user, recruitment2));
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        //when
        User findUser = userRepository.findRecruitmentInterestById(userId).get();

        //then
        assertAll(
                () -> assertThat(findUser.getRecruitmentInterests().size()).isEqualTo(2),
                () -> assertThat(findUser.getRecruitmentInterests().get(0).getRecruitment()).isEqualTo(recruitment1)
        );
    }
}