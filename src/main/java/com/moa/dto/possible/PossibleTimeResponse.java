package com.moa.dto.possible;

import com.moa.domain.possible.PossibleTime;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PossibleTimeResponse {
    private final String nickname;
    private List<PossibleTimeData> possibleTimeData ;

    @Builder
    public PossibleTimeResponse(String nickname, List<PossibleTime> possibleTimes) {
        this.nickname = nickname;
        setPossibleTimeData(possibleTimes);
    }

    public void setPossibleTimeData(List<PossibleTime> possibleTimes) {
        this.possibleTimeData = getPossibleTimeData(possibleTimes);
    }

    public static List<PossibleTimeData> getPossibleTimeData(List<PossibleTime> possibleTimes) {
        return possibleTimes.stream()
                .map(p -> PossibleTimeData.builder()
                        .day(p.getDay().name())
                        .startTime(p.getStartTime())
                        .endTime(p.getEndTime()).build())
                .toList();
    }
}
