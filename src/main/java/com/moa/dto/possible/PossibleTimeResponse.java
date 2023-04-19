package com.moa.dto.possible;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moa.domain.possible.PossibleTime;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PossibleTimeResponse {
    private final String nickname;
    @NotEmpty
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Seoul")
    private List<LocalDateTime> possibleTimeData;

    @Builder
    public PossibleTimeResponse(String nickname, List<PossibleTime> possibleTimes) {
        this.nickname = nickname;
        setPossibleTimeData(possibleTimes);
    }

    public void setPossibleTimeData(List<PossibleTime> possibleTimes) {
        this.possibleTimeData = getPossibleTimeData(possibleTimes);
    }

    public static List<LocalDateTime> getPossibleTimeData(List<PossibleTime> possibleTimes) {
        List<LocalDateTime> timeList = new ArrayList<>();

        for (PossibleTime possibleTime : possibleTimes) {
            addTime(possibleTime, timeList);
        }

        return timeList;
    }

    private static void addTime(PossibleTime possibleTime, List<LocalDateTime> timeList)  {
        LocalDateTime startTime = possibleTime.getStartTime();
        LocalDateTime endTime = possibleTime.getEndTime();

        timeList.add(startTime);

        while (endTime.isAfter(startTime)) {
            startTime = startTime.plusMinutes(30);
            timeList.add(startTime);
        }
    }
}
