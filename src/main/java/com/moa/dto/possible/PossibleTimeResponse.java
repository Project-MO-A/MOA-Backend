package com.moa.dto.possible;

import com.moa.domain.possible.PossibleTime;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PossibleTimeResponse {
    private final String nickname;
    @NotEmpty
    private List<String> possibleTimeData;

    @Builder
    public PossibleTimeResponse(String nickname, List<PossibleTime> possibleTimes) {
        this.nickname = nickname;
        setPossibleTimeData(possibleTimes);
    }

    public void setPossibleTimeData(List<PossibleTime> possibleTimes) {
        this.possibleTimeData = getPossibleTimeData(possibleTimes);
    }

    public static List<String> getPossibleTimeData(List<PossibleTime> possibleTimes) {
        List<String> timeList = new ArrayList<>();

        for (PossibleTime possibleTime : possibleTimes) {
            addTime(possibleTime, timeList);
        }

        return timeList;
    }

    private static void addTime(PossibleTime possibleTime, List<String> timeList)  {
        LocalDateTime startTime = possibleTime.getStartTime();
        LocalDateTime endTime = possibleTime.getEndTime();

        do {
            String time = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            timeList.add(time);

            startTime = startTime.plusMinutes(30);
        } while (endTime.isAfter(startTime) || endTime.isEqual(startTime));
    }
}
