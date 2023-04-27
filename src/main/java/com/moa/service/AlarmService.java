package com.moa.service;

import com.moa.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public Long post(final AlarmType alarmType, final Long receiveUserId, final Long relateId) {
        User user = userRepository.getReferenceById(receiveUserId);
        return alarmRepository.save(Alarm.builder()
                .alarmType(alarmType)
                .user(user)
                .relateId(relateId)
                .build()).getId();
    }

    public List<Long> postAll(final AlarmType alarmType, final List<Long> receiveUserIds, final Long relateId) {
        List<Long> noticeAlarm = new ArrayList<>();
        for (Long userId : receiveUserIds) {
            noticeAlarm.add(post(alarmType, userId, relateId));
        }
        return noticeAlarm;
    }
}
