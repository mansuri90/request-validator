package com.theadex.requestvalidator.service;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.domain.HourlyStats;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface HourlyStatsService {
    void incrementInvalidCount(Customer customer, Instant time);

    void incrementRequestCount(Customer customer, Instant time);

    List<HourlyStats> getStatsByCustomerAndDate(Customer customer, LocalDate localDate);
}
