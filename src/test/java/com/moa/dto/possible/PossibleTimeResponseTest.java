package com.moa.dto.possible;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.possible.PossibleTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PossibleTimeResponseTest {

    @DisplayName("참여 시간 반환값 테스트")
    @Test
    void getPossibleTimeData() {
        List<PossibleTime> list = new ArrayList<>();


        PossibleTime build1 = PossibleTime.builder()
                .startTime(LocalDateTime.of(2023, 4, 15, 12, 0))
                .endTime(LocalDateTime.of(2023, 4, 15, 18, 0))
                .applimentMember(ApplimentMember.builder().build())
                .build();
        PossibleTime build2 = PossibleTime.builder()
                .startTime(LocalDateTime.of(2023, 4, 16, 12, 0))
                .endTime(LocalDateTime.of(2023, 4, 16, 15, 0))
                .applimentMember(ApplimentMember.builder().build())
                .build();
        PossibleTime build3 = PossibleTime.builder()
                .startTime(LocalDateTime.of(2023, 4, 17, 12, 0))
                .endTime(LocalDateTime.of(2023, 4, 17, 14, 0))
                .applimentMember(ApplimentMember.builder().build())
                .build();
        list.add(build1);
        list.add(build2);
        list.add(build3);

        List<LocalDateTime> possibleTimeData = PossibleTimeResponse.getPossibleTimeData(list);

        for (int i = 1; i < possibleTimeData.size(); i++) {
            LocalDateTime data = possibleTimeData.get(i-1);
            assertThat(data.isBefore(possibleTimeData.get(i)));
        }
    }
}