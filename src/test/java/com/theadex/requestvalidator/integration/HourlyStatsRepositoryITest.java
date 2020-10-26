package com.theadex.requestvalidator.integration;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.domain.HourlyStats;
import com.theadex.requestvalidator.repository.CustomerRepository;
import com.theadex.requestvalidator.repository.HourlyStatsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HourlyStatsRepositoryITest {
    @Autowired
    private HourlyStatsRepository hourlyStatsRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void incrementInvalidCountOfNonExistingStats() {
        final Customer customer = customerRepository.save(new Customer(null, "name1", true));
        final LocalDate currentDate = LocalDate.now();
        final LocalTime currentHour = LocalTime.now().truncatedTo(ChronoUnit.HOURS);
        final int affectedRowsCount = hourlyStatsRepository.incrementInvalidCountIfExist(customer.getId(), currentDate, currentHour);

        assertThat(affectedRowsCount).isZero();

        final Optional<HourlyStats> hourlyStatsOptional = hourlyStatsRepository.findByCustomerAndDateAndTime(customer, currentDate, currentHour);
        assertThat(hourlyStatsOptional).isNotPresent();
    }

    @Test
    void incrementInvalidCountOfExistingStats() {
        final LocalDate currentDate = LocalDate.now();
        final LocalTime currentHour = LocalTime.now().truncatedTo(ChronoUnit.HOURS);
        final Customer customer = customerRepository.save(new Customer(null, "name1", true));

        HourlyStats hourlyStats = new HourlyStats(null, customer, currentDate, currentHour, 0L, 1L);
        hourlyStatsRepository.save(hourlyStats);

        final int affectedRowsCount = hourlyStatsRepository.incrementInvalidCountIfExist(customer.getId(), currentDate, currentHour);
        assertThat(affectedRowsCount).isEqualTo(1);

        final Optional<HourlyStats> hourlyStatsOptional = hourlyStatsRepository.findByCustomerAndDateAndTime(customer, currentDate, currentHour);
        assertThat(hourlyStatsOptional).isPresent();

        final HourlyStats retrievedHourlyStats = hourlyStatsOptional.get();
        assertThat(retrievedHourlyStats.getInvalidCount()).isEqualTo(2L);
        assertThat(retrievedHourlyStats.getRequestCount()).isZero();
        assertThat(retrievedHourlyStats.getDate()).isEqualTo(currentDate);
        assertThat(retrievedHourlyStats.getTime()).isEqualTo(currentHour);
    }

    @Test
    void incrementRequestCountOfNonExistingStats() {
        final Customer customer = customerRepository.save(new Customer(null, "name1", true));
        final LocalDate currentDate = LocalDate.now();
        final LocalTime currentHour = LocalTime.now().truncatedTo(ChronoUnit.HOURS);
        final int affectedRowsCount = hourlyStatsRepository.incrementRequestCountIfExist(customer.getId(), currentDate, currentHour);

        assertThat(affectedRowsCount).isZero();

        final Optional<HourlyStats> hourlyStatsOptional = hourlyStatsRepository.findByCustomerAndDateAndTime(customer, currentDate, currentHour);
        assertThat(hourlyStatsOptional).isNotPresent();
    }

    @Test
    void incrementRequestCountOfExistingStats() {
        final LocalDate currentDate = LocalDate.now();
        final LocalTime currentHour = LocalTime.now().truncatedTo(ChronoUnit.HOURS);
        final Customer customer = customerRepository.save(new Customer(null, "name1", true));

        final HourlyStats hourlyStats = new HourlyStats(null, customer, currentDate, currentHour, 1L, 1L);
        hourlyStatsRepository.save(hourlyStats);

        final int affectedRowsCount = hourlyStatsRepository.incrementRequestCountIfExist(customer.getId(), currentDate, currentHour);
        assertThat(affectedRowsCount).isEqualTo(1);

        final Optional<HourlyStats> hourlyStatsOptional = hourlyStatsRepository.findByCustomerAndDateAndTime(customer, currentDate, currentHour);
        assertThat(hourlyStatsOptional).isPresent();

        final HourlyStats retrievedHourlyStats = hourlyStatsOptional.get();
        assertThat(retrievedHourlyStats.getRequestCount()).isEqualTo(2L);
        assertThat(retrievedHourlyStats.getInvalidCount()).isEqualTo(1L);
        assertThat(retrievedHourlyStats.getDate()).isEqualTo(currentDate);
        assertThat(retrievedHourlyStats.getTime()).isEqualTo(currentHour);
    }
}
