package com.moa.service;

import com.moa.domain.user.Alarm;
import com.moa.domain.user.AlarmRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.moa.constant.TestConst.EMAIL;
import static com.moa.constant.TestConst.USER;
import static com.moa.domain.user.AlarmType.NOTICE_POST;
import static com.moa.domain.user.AlarmType.RECRUITMENT_COMPLETE;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class AlarmServiceTest {
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    EntityManager em;

    private static final Long RELATE_ID = 15L;
    @BeforeEach
    void setUp() {
        userRepository.save(USER);
    }

    @DisplayName("post - 알람 생성에 성공한다.")
    @Test
    void post() {
        //given
        User user = userRepository.findByEmail(EMAIL).get();

        //when
        Long alarmId = alarmService.post(RECRUITMENT_COMPLETE, user.getId(), RELATE_ID);

        //then
        Alarm findAlarm = alarmRepository.findById(alarmId).orElseThrow();

        System.out.println(findAlarm.getMessage());
        System.out.println(findAlarm.getRedirectURI());

        assertThat(findAlarm.getAlarmType()).isEqualTo(RECRUITMENT_COMPLETE);
        assertThat(findAlarm.getRedirectURI()).isEqualTo(RECRUITMENT_COMPLETE.getRedirectURI(RELATE_ID));
        assertThat(findAlarm.getMessage()).isEqualTo(RECRUITMENT_COMPLETE.getMessage(RELATE_ID));
        assertThat(user.getAlarms().contains(findAlarm)).isTrue();
    }

    @DisplayName("postAll - 여러개의 알람 생성에 성공한다.")
    @Test
    void postAll() {
        //given
        List<Long> userId = new ArrayList<>();
        User user1 = userRepository.findByEmail(EMAIL).get();
        User user2 = User.builder().email("test1@test.com").build();
        User user3 = User.builder().email("test2@test.com").build();
        userRepository.save(user2);
        userRepository.save(user3);

        userId.add(user1.getId());
        userId.add(user2.getId());
        userId.add(user3.getId());

        //when
        List<Long> alarmId = alarmService.postAll(NOTICE_POST, userId, RELATE_ID);

        //then
        Alarm findAlarm = alarmRepository.findById(alarmId.get(0)).orElseThrow();

        System.out.println(findAlarm.getMessage());
        System.out.println(findAlarm.getRedirectURI());

        assertThat(user1.getAlarms().get(0).getAlarmType())
                .isEqualTo(user2.getAlarms().get(0).getAlarmType())
                .isEqualTo(user3.getAlarms().get(0).getAlarmType());
        assertThat(findAlarm.getMessage()).isEqualTo(NOTICE_POST.getMessage(RELATE_ID));
        assertThat(findAlarm.getRedirectURI()).isEqualTo(NOTICE_POST.getRedirectURI(RELATE_ID));
    }
}