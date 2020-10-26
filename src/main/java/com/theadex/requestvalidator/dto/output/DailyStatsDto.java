package com.theadex.requestvalidator.dto.output;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class DailyStatsDto {
    private @NonNull List<HourlyStatsDto> hourlyStats;
    private @NonNull Long total;
}
