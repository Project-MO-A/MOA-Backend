package com.moa.dto.possible;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PossibleTimeRequest(
        @NotNull @Valid
        List<PossibleTimeData> possibleTimeDataList
) {
}
