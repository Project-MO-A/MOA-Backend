package com.moa.dto.possible;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record PossibleTimeData (
        @NotNull String day,
        LocalTime startTime,
        LocalTime endTime
) {
}
