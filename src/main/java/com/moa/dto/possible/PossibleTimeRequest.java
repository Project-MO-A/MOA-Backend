package com.moa.dto.possible;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.possible.PossibleTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record PossibleTimeRequest(
        @NotNull @Valid
        List<LocalDateTime> possibleTimeDataList
) {
    public void sortTime() {
        Collections.sort(possibleTimeDataList);
    }

    public List<PossibleTime> getEntityList(ApplimentMember applimentMember) {
        Collections.sort(this.possibleTimeDataList);
        List<PossibleTime> possibleTimes = new ArrayList<>();

        LocalDateTime before = possibleTimeDataList.get(0);
        int startIdx = 0;

        for (int idx = 1; idx < this.possibleTimeDataList.size(); idx++) {
            LocalDateTime current = possibleTimeDataList.get(idx);
            Duration duration = Duration.between(before, current);
            long minutes = duration.toMinutes();

            if (minutes > 30) {
                PossibleTime possibleTime = PossibleTime.builder()
                        .applimentMember(applimentMember)
                        .startTime(possibleTimeDataList.get(startIdx))
                        .endTime(before).build();
                possibleTimes.add(possibleTime);
                startIdx = idx;
            }

            if (idx == possibleTimeDataList.size() - 1) {
                PossibleTime possibleTime = PossibleTime.builder()
                        .applimentMember(applimentMember)
                        .startTime(possibleTimeDataList.get(startIdx))
                        .endTime(possibleTimeDataList.get(idx)).build();
                possibleTimes.add(possibleTime);
            }

            before = current;
        }

        return possibleTimes;
    }
}
