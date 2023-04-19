package com.moa.dto.possible;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.possible.PossibleTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class PossibleTimeRequestTest {

    @DisplayName("시간 그룹핑 테스트")
    @Test
    void getEntityList() {
        List<LocalDateTime> list = List.of(
                LocalDateTime.of(2023, 4, 19, 9, 0),
                LocalDateTime.of(2023, 4, 19, 9, 30),
                LocalDateTime.of(2023, 4, 19, 10, 0),
                LocalDateTime.of(2023, 4, 19, 10, 30),
                LocalDateTime.of(2023, 4, 19, 11, 0),
                LocalDateTime.of(2023, 4, 19, 11, 30),
                LocalDateTime.of(2023, 4, 19, 12, 0),

                LocalDateTime.of(2023, 4, 19, 15, 0),
                LocalDateTime.of(2023, 4, 19, 15, 30),
                LocalDateTime.of(2023, 4, 19, 16, 0),

                LocalDateTime.of(2023, 4, 19, 19, 0),
                LocalDateTime.of(2023, 4, 19, 19, 30),

                LocalDateTime.of(2023, 4, 19, 21, 0),
                LocalDateTime.of(2023, 4, 19, 21, 30),

                LocalDateTime.of(2023, 4, 19, 23, 0)
        );

        List<LocalDateTime> request = new ArrayList<>();
        request.addAll(list);

        PossibleTimeRequest possibleTimeRequest = new PossibleTimeRequest(request);
        List<PossibleTime> entityList = possibleTimeRequest.getEntityList(ApplimentMember.builder().build());

        for (PossibleTime possibleTime : entityList) {
            System.out.println(possibleTime);
        }
    }
}