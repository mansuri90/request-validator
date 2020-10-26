package com.theadex.requestvalidator.integration;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.domain.HourlyStats;
import com.theadex.requestvalidator.repository.HourlyStatsRepository;
import com.theadex.requestvalidator.service.CustomerService;
import com.theadex.requestvalidator.service.HourlyStatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HourlyStatsServiceITest {
    @Autowired
    private HourlyStatsService hourlyStatsService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private HourlyStatsRepository hourlyStatsRepository;

    @Test
    void incrementInvalidCountOfNonExistingStats() {
        final Customer customer = customerService.save(new Customer(null, "name1", true));
        final ZonedDateTime zonedDateTime = ZonedDateTime.now();

        hourlyStatsService.incrementInvalidCount(customer, zonedDateTime.toInstant());

        final LocalDate currentDate = zonedDateTime.toLocalDate();
        final LocalTime currentHour = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, currentDate);

        assertThat(dailyStats)
                .isNotNull()
                .hasSize(1);

        final HourlyStats hourlyStats = dailyStats.get(0);
        assertThat(hourlyStats).isNotNull();

        assertThat(hourlyStats.getInvalidCount()).isEqualTo(1L);
        assertThat(hourlyStats.getRequestCount()).isZero();
        assertThat(hourlyStats.getDate()).isEqualTo(currentDate);
        assertThat(hourlyStats.getTime()).isEqualTo(currentHour);
    }

    @Test
    void incrementInvalidCountOfExistingStats() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.now();
        final LocalDate currentDate = zonedDateTime.toLocalDate();
        final LocalTime currentHour = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);

        final Customer customer = customerService.save(new Customer(null, "name1", true));

        final HourlyStats hourlyStats = new HourlyStats(null, customer, currentDate, currentHour, 1L, 1L);
        hourlyStatsRepository.save(hourlyStats);

        hourlyStatsService.incrementInvalidCount(customer, zonedDateTime.toInstant());

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, currentDate);

        assertThat(dailyStats)
                .isNotNull()
                .hasSize(1);

        final HourlyStats retrievedHourlyStats = dailyStats.get(0);
        assertThat(retrievedHourlyStats).isNotNull();

        assertThat(retrievedHourlyStats.getInvalidCount()).isEqualTo(2L);
        assertThat(retrievedHourlyStats.getRequestCount()).isEqualTo(1L);
        assertThat(retrievedHourlyStats.getDate()).isEqualTo(currentDate);
        assertThat(retrievedHourlyStats.getTime()).isEqualTo(currentHour);
    }

    @Test
    void incrementRequestCountOfNonExistingStats() {
        final Customer customer = customerService.save(new Customer(null, "name1", true));
        final ZonedDateTime zonedDateTime = ZonedDateTime.now();

        hourlyStatsService.incrementRequestCount(customer, zonedDateTime.toInstant());

        final LocalDate currentDate = zonedDateTime.toLocalDate();
        final LocalTime currentHour = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, currentDate);

        assertThat(dailyStats)
                .isNotNull()
                .hasSize(1);

        final HourlyStats hourlyStats = dailyStats.get(0);
        assertThat(hourlyStats).isNotNull();

        assertThat(hourlyStats.getRequestCount()).isEqualTo(1L);
        assertThat(hourlyStats.getInvalidCount()).isZero();
        assertThat(hourlyStats.getDate()).isEqualTo(currentDate);
        assertThat(hourlyStats.getTime()).isEqualTo(currentHour);
    }

    @Test
    void incrementRequestCountOfExistingStats() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.now();
        final LocalDate currentDate = zonedDateTime.toLocalDate();
        final LocalTime currentHour = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);

        final Customer customer = customerService.save(new Customer(null, "name1", true));

        final HourlyStats hourlyStats = new HourlyStats(null, customer, currentDate, currentHour, 1L, 1L);
        hourlyStatsRepository.save(hourlyStats);

        hourlyStatsService.incrementRequestCount(customer, zonedDateTime.toInstant());

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, currentDate);

        assertThat(dailyStats)
                .isNotNull()
                .hasSize(1);

        final HourlyStats retrievedHourlyStats = dailyStats.get(0);
        assertThat(retrievedHourlyStats).isNotNull();

        assertThat(retrievedHourlyStats.getRequestCount()).isEqualTo(2L);
        assertThat(retrievedHourlyStats.getInvalidCount()).isEqualTo(1L);
        assertThat(retrievedHourlyStats.getDate()).isEqualTo(currentDate);
        assertThat(retrievedHourlyStats.getTime()).isEqualTo(currentHour);
    }

    @Test
    void multipleDaysStatsTest() {
        final ZonedDateTime currentZonedDateTime = ZonedDateTime.now();
        final ZonedDateTime yesterdayZonedDateTime = currentZonedDateTime.minusDays(1);
        final LocalDate currentDate = currentZonedDateTime.toLocalDate();
        final LocalDate yesterday = yesterdayZonedDateTime.toLocalDate();
        final LocalTime hour = currentZonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);

        final Customer customer = customerService.save(new Customer(null, "name1", true));

        //today requests
        hourlyStatsService.incrementRequestCount(customer, currentZonedDateTime.toInstant());
        hourlyStatsService.incrementRequestCount(customer, currentZonedDateTime.toInstant());
        hourlyStatsService.incrementRequestCount(customer, currentZonedDateTime.toInstant());
        //today invalids
        hourlyStatsService.incrementInvalidCount(customer, currentZonedDateTime.toInstant());
        hourlyStatsService.incrementInvalidCount(customer, currentZonedDateTime.toInstant());

        //yesterday requests
        hourlyStatsService.incrementRequestCount(customer, yesterdayZonedDateTime.toInstant());
        hourlyStatsService.incrementRequestCount(customer, yesterdayZonedDateTime.toInstant());
        //yesterday invalids
        hourlyStatsService.incrementInvalidCount(customer, yesterdayZonedDateTime.toInstant());
        hourlyStatsService.incrementInvalidCount(customer, yesterdayZonedDateTime.toInstant());
        hourlyStatsService.incrementInvalidCount(customer, yesterdayZonedDateTime.toInstant());

        final List<HourlyStats> todayStats = hourlyStatsService.getStatsByCustomerAndDate(customer, currentDate);

        assertThat(todayStats)
                .isNotNull()
                .hasSize(1);
        final HourlyStats currentHourStats = todayStats.get(0);
        assertThat(currentHourStats).isNotNull();

        assertThat(currentHourStats.getRequestCount()).isEqualTo(3L);
        assertThat(currentHourStats.getInvalidCount()).isEqualTo(2L);
        assertThat(currentHourStats.getDate()).isEqualTo(currentDate);
        assertThat(currentHourStats.getTime()).isEqualTo(hour);

        final List<HourlyStats> yesterdayStats = hourlyStatsService.getStatsByCustomerAndDate(customer, yesterday);

        assertThat(yesterdayStats)
                .isNotNull()
                .hasSize(1);
        final HourlyStats yesterdayAtCurrentHourStats = yesterdayStats.get(0);
        assertThat(yesterdayAtCurrentHourStats).isNotNull();

        assertThat(yesterdayAtCurrentHourStats.getRequestCount()).isEqualTo(2L);
        assertThat(yesterdayAtCurrentHourStats.getInvalidCount()).isEqualTo(3L);
        assertThat(yesterdayAtCurrentHourStats.getDate()).isEqualTo(yesterday);
        assertThat(yesterdayAtCurrentHourStats.getTime()).isEqualTo(hour);
    }

    @Test
    void multipleHoursStatsTest() {
        final ZonedDateTime currentZonedDateTime = ZonedDateTime.now();
        final LocalDate currentDate = currentZonedDateTime.toLocalDate();
        final LocalTime currentHour = currentZonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);

        final LocalTime startOfDayHour = currentDate.atStartOfDay(currentZonedDateTime.getZone()).toLocalTime().truncatedTo(ChronoUnit.HOURS);
        final LocalTime anotherHour = startOfDayHour.equals(currentHour) ? startOfDayHour.plusHours(1) : startOfDayHour;

        final Customer customer = customerService.save(new Customer(null, "name1", true));

        //current hour requests
        hourlyStatsService.incrementRequestCount(customer, currentZonedDateTime.toInstant());
        hourlyStatsService.incrementRequestCount(customer, currentZonedDateTime.toInstant());
        hourlyStatsService.incrementRequestCount(customer, currentZonedDateTime.toInstant());
        //current hour invalids
        hourlyStatsService.incrementInvalidCount(customer, currentZonedDateTime.toInstant());
        hourlyStatsService.incrementInvalidCount(customer, currentZonedDateTime.toInstant());

        //anotherHour requests
        final Instant anotherHourInstant = currentDate.atTime(anotherHour).atZone(currentZonedDateTime.getZone()).toInstant();
        hourlyStatsService.incrementRequestCount(customer, anotherHourInstant);
        hourlyStatsService.incrementRequestCount(customer, anotherHourInstant);
        //anotherHour invalids
        hourlyStatsService.incrementInvalidCount(customer, anotherHourInstant);
        hourlyStatsService.incrementInvalidCount(customer, anotherHourInstant);
        hourlyStatsService.incrementInvalidCount(customer, anotherHourInstant);

        final List<HourlyStats> todayStats = hourlyStatsService.getStatsByCustomerAndDate(customer, currentDate);

        todayStats.sort((st1, st2) -> {
            if (currentHour.isBefore(anotherHour)) {
                return st1.getTime().compareTo(st2.getTime());
            } else {
                return st2.getTime().compareTo(st1.getTime());
            }
        });

        assertThat(todayStats)
                .isNotNull()
                .hasSize(2);
        final HourlyStats currentHourStats = todayStats.get(0);
        assertThat(currentHourStats).isNotNull();

        assertThat(currentHourStats.getRequestCount()).isEqualTo(3L);
        assertThat(currentHourStats.getInvalidCount()).isEqualTo(2L);
        assertThat(currentHourStats.getDate()).isEqualTo(currentDate);
        assertThat(currentHourStats.getTime()).isEqualTo(currentHour);

        final HourlyStats anotherHourStats = todayStats.get(1);
        assertThat(anotherHourStats).isNotNull();

        assertThat(anotherHourStats.getRequestCount()).isEqualTo(2L);
        assertThat(anotherHourStats.getInvalidCount()).isEqualTo(3L);
        assertThat(anotherHourStats.getDate()).isEqualTo(currentDate);
        assertThat(anotherHourStats.getTime()).isEqualTo(anotherHour);
    }

}
