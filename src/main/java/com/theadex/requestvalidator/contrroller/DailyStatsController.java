package com.theadex.requestvalidator.contrroller;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.domain.HourlyStats;
import com.theadex.requestvalidator.dto.output.DailyStatsDto;
import com.theadex.requestvalidator.dto.output.HourlyStatsDto;
import com.theadex.requestvalidator.service.CustomerService;
import com.theadex.requestvalidator.service.HourlyStatsService;
import io.swagger.annotations.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily-stats")
@Api(tags = "Daily Stats")
public class DailyStatsController {
    private final @NonNull CustomerService customerService;
    private final @NonNull HourlyStatsService hourlyStatsService;

    @ApiOperation(value = "Retrieves daily stats of valid and invalid requests grouped by hour")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "Successful"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "missing or malformed customerID or invalid date format"),
            @ApiResponse(code = SC_NOT_FOUND, message = "no customer with provided customerID found")
    })
    @GetMapping("/{date}/{customerID}")
    public DailyStatsDto dailyStats(@PathVariable("customerID") Long customerID,
                                    @ApiParam(format ="yyyy-MM-dd", example = "2020-01-20")
                                    @DateTimeFormat(iso = ISO.DATE) @PathVariable("date") LocalDate date) {
        final Customer customer = customerService.getCustomerById(customerID);

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, date);
        final List<HourlyStatsDto> hourlyStatsDtoList = dailyStats.stream()
                .map(stats -> new HourlyStatsDto(stats.getTime(), stats.getRequestCount(), stats.getInvalidCount()))
                .collect(Collectors.toList());

        final long total = dailyStats.stream()
                .mapToLong(hourlyStats -> hourlyStats.getRequestCount() + hourlyStats.getInvalidCount())
                .sum();

        return new DailyStatsDto(hourlyStatsDtoList, total);
    }
}
