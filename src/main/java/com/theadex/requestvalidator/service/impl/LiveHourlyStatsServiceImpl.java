package com.theadex.requestvalidator.service.impl;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.domain.HourlyStats;
import com.theadex.requestvalidator.repository.HourlyStatsRepository;
import com.theadex.requestvalidator.service.HourlyStatsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveHourlyStatsServiceImpl implements HourlyStatsService {
    private final @NonNull HourlyStatsRepository hourlyStatsRepository;

    @Override
    public void incrementInvalidCount(@NonNull Customer customer, @NonNull Instant instant) {
        final ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        final LocalTime hour = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);
        final LocalDate date = zonedDateTime.toLocalDate();

        final @NonNull Long customerId = customer.getId();

        int affectedRowsCount = hourlyStatsRepository.incrementInvalidCountIfExist(customerId, date, hour);
        if (affectedRowsCount < 1) {//hourlyStats did not exist so we will try to insert it
            HourlyStats hourlyStats = new HourlyStats(null, customer, date, hour, 0L, 1L);
            try {
                hourlyStatsRepository.save(hourlyStats);
                hourlyStatsRepository.flush();
            } catch (DataIntegrityViolationException e) {
                if (log.isDebugEnabled()) {
                    log.debug("could not save hourlyStats. it may be related to concurrent insert for the same hour", e);
                }
                //hourlyStats related to this hour has been inserted by another transaction, so we just update it.
                affectedRowsCount = hourlyStatsRepository.incrementInvalidCountIfExist(customerId, date, hour);
                if (affectedRowsCount < 1) {
                    throw new IllegalStateException(String.format("neither insert nor update of hourlyStats were successful. customerId: %d, instant: %s, requestStatus:invalid", customerId, instant));
                }
            }
        }
    }

    @Override
    public void incrementRequestCount(@NonNull Customer customer, @NonNull Instant instant) {
        final ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        final LocalTime hour = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);
        final LocalDate date = zonedDateTime.toLocalDate();

        final @NonNull Long customerId = customer.getId();

        int affectedRowsCount = hourlyStatsRepository.incrementRequestCountIfExist(customerId, date, hour);
        if (affectedRowsCount < 1) {//hourlyStats did not exist so we will try to insert it
            HourlyStats hourlyStats = new HourlyStats(null, customer, date, hour, 1L, 0L);
            try {
                hourlyStatsRepository.save(hourlyStats);
                hourlyStatsRepository.flush();
            } catch (DataIntegrityViolationException e) {
                if (log.isDebugEnabled()) {
                    log.debug("could not save hourlyStats. it may be related to concurrent insert for the same hour", e);
                }
                //hourlyStats related to this hour has been inserted by another transaction, so we just update it.
                affectedRowsCount = hourlyStatsRepository.incrementRequestCountIfExist(customerId, date, hour);
                if (affectedRowsCount < 1) {
                    throw new IllegalStateException(String.format("neither insert nor update of hourlyStats were successful. customerId: %d, instant: %s, requestStatus:valid", customerId, instant));
                }
            }
        }
    }

    @Override
    public List<HourlyStats> getStatsByCustomerAndDate(@NonNull Customer customer, @NonNull LocalDate localDate) {
        return hourlyStatsRepository.findByCustomerAndDate(customer, localDate);
    }
}
