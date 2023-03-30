package com.moa.service;

import com.moa.domain.user.Alarm;
import com.moa.domain.user.AlarmRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.moa.domain.user.AlarmType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AlarmServiceUnitTest {
    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AlarmService alarmService;

    private static final Long RELATE_ID = 15L;
    private static final User REFERENCE_USER = User.builder().build();

    @DisplayName("post - 알람 생성에 성공한다.")
    @Test
    void post() {
        //given
        Long userId = 1L;

        given(userRepository.getReferenceById(userId))
                .willReturn(REFERENCE_USER);
        given(alarmRepository.save(any(Alarm.class)))
                .willReturn(Alarm.builder()
                        .user(REFERENCE_USER)
                        .relateId(RELATE_ID)
                        .alarmType(RECRUITMENT_COMPLETE)
                        .build());

        //when
        Long post = alarmService.post(PARTICIPATION_APPROVAL, userId, RELATE_ID);

        //then
        assertAll(
                () -> verify(userRepository).getReferenceById(userId),
                () -> verify(alarmRepository).save(any(Alarm.class))
        );
    }

    @DisplayName("post - 존재하지 않는 UserId를 전달받을 경우 예외가 발생한다.")
    @Test
    void postFail() {
        //given
        Long invalidUserId = 99L;

        given(userRepository.getReferenceById(invalidUserId))
                .willReturn(REFERENCE_USER);
        given(alarmRepository.save(any(Alarm.class)))
                .willThrow(EntityNotFoundException.class);

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> alarmService.post(PARTICIPATION_REQUEST, invalidUserId, RELATE_ID))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(userRepository).getReferenceById(invalidUserId)
        );
    }

    @DisplayName("postAll - 여러개의 알람 생성에 성공한다.")
    @Test
    void postAll() {
        //given
        List<Long> idList = List.of(10L, 12L, 15L);

        given(userRepository.getReferenceById(any(Long.class)))
                .willReturn(REFERENCE_USER);
        given(alarmRepository.save(any(Alarm.class)))
                .willReturn(Alarm.builder()
                        .user(REFERENCE_USER)
                        .relateId(RELATE_ID)
                        .alarmType(RECRUITMENT_COMPLETE)
                        .build());

        //when
        List<Long> alarmId = alarmService.postAll(RECRUITMENT_COMPLETE, idList, RELATE_ID);

        //then
        assertAll(
                () -> assertThat(alarmId.size()).isEqualTo(idList.size()),
                () -> verify(userRepository, times(idList.size())).getReferenceById(any(Long.class)),
                () -> verify(alarmRepository, times(idList.size())).save(any(Alarm.class))
        );
    }

    @DisplayName("postAll - 존재하지 않는 UserId List를 전달받을 경우 예외가 발생한다.")
    @Test
    void postAllFail() {
        //given
        List<Long> idList = List.of(10L, 12L, 15L);

        given(userRepository.getReferenceById(any(Long.class)))
                .willReturn(REFERENCE_USER);
        given(alarmRepository.save(any(Alarm.class)))
                .willThrow(EntityNotFoundException.class);

        //when
        assertAll(
                () -> assertThatThrownBy(() -> alarmService.postAll(RECRUITMENT_COMPLETE, idList, RELATE_ID))
                        .isExactlyInstanceOf(EntityNotFoundException.class),
                () -> verify(userRepository, times(1)).getReferenceById(any(Long.class)),
                () -> verify(alarmRepository, times(1)).save(any(Alarm.class))
        );
    }
}