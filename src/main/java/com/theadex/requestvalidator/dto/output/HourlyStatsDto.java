package com.theadex.requestvalidator.dto.output;

import lombok.NonNull;
import lombok.Value;

import java.time.LocalTime;

@Value
public class HourlyStatsDto {
    @NonNull LocalTime time;
    @NonNull Long requestCount;
    @NonNull Long invalidCount;
}
