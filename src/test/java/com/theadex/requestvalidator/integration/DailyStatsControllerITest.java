package com.theadex.requestvalidator.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.service.CustomerService;
import com.theadex.requestvalidator.service.HourlyStatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class DailyStatsControllerITest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private HourlyStatsService hourlyStatsService;

    private final String dailyStatsUriTemplate = "/daily-stats/{date}/{customerID}";

    @Test
    void givenValidCustomerIdAndDailyStats_whenRequestStats_thenOkStatus() throws Exception {
        final Customer customer = customerService.save(new Customer(null, "name_1", true));

        final ZonedDateTime currentZonedDateTime = ZonedDateTime.now();
        final ZonedDateTime yesterdayZonedDateTime = currentZonedDateTime.minusDays(1);
        final LocalDate currentDate = currentZonedDateTime.toLocalDate();
        final LocalDate yesterday = yesterdayZonedDateTime.toLocalDate();
        final LocalTime hour = currentZonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);

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
        hourlyStatsService.incrementInvalidCount(customer, yesterdayZonedDateTime.toInstant());

        mockMvc.perform(get(dailyStatsUriTemplate, currentDate, customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("total").value(5))
                .andExpect(jsonPath("hourlyStats").isArray())
                .andExpect(jsonPath("hourlyStats", hasSize(1)))
                .andExpect(jsonPath("hourlyStats.[0].requestCount", is(3)))
                .andExpect(jsonPath("hourlyStats.[0].invalidCount", is(2)))
                .andExpect(jsonPath("hourlyStats.[0].time", is(hour.format(ISO_LOCAL_TIME))));

        mockMvc.perform(get(dailyStatsUriTemplate, yesterday, customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("total").value(6))
                .andExpect(jsonPath("hourlyStats").isArray())
                .andExpect(jsonPath("hourlyStats", hasSize(1)))
                .andExpect(jsonPath("hourlyStats.[0].requestCount", is(2)))
                .andExpect(jsonPath("hourlyStats.[0].invalidCount", is(4)))
                .andExpect(jsonPath("hourlyStats.[0].time", is(hour.format(ISO_LOCAL_TIME))));
    }

    @Test
    void givenInvalidCustomerId_whenRequestStats_thenNotFoundStatus() throws Exception {
        mockMvc.perform(get(dailyStatsUriTemplate, LocalDate.now(), 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", is("no customer with Id '1' were found")));
    }

    @Test
    void givenMalformedDate_whenRequestStats_thenBadRequestStatus() throws Exception {
        mockMvc.perform(get(dailyStatsUriTemplate, "Malformed_Date", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error",
                        is("date parameter must be convertible to type class java.time.LocalDate")));
    }

}
