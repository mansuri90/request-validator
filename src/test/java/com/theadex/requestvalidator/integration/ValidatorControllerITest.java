package com.theadex.requestvalidator.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.domain.HourlyStats;
import com.theadex.requestvalidator.dto.input.RequestDto;
import com.theadex.requestvalidator.repository.HourlyStatsRepository;
import com.theadex.requestvalidator.service.CustomerService;
import com.theadex.requestvalidator.service.HourlyStatsService;
import com.theadex.requestvalidator.service.IpBlackListService;
import com.theadex.requestvalidator.service.UserAgentBlackListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class ValidatorControllerITest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private HourlyStatsService hourlyStatsService;
    @Autowired
    private HourlyStatsRepository hourlyStatsRepository;
    @Autowired
    private IpBlackListService ipBlackListService;
    @Autowired
    private UserAgentBlackListService userAgentBlackListService;

    private final String validationPath = "/request-validator";

    @Test
    void givenValidCustomerId_whenValidateRequest_thenIncrementRequestCount() throws Exception {
        final Customer customer = customerService.save(new Customer(null, "name_1", true));
        final RequestDto requestDto = new RequestDto(customer.getId(), 1L, "aaaaaaaa-bbbb-cccc-1111-222222222222",
                "123.234.56.78", 1500000000L);

        mockMvc.perform(post(validationPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto))
        ).andExpect(status().isOk());

        final Instant instant = Instant.ofEpochSecond(requestDto.getTimestamp());
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        final LocalDate requestDate = zonedDateTime.toLocalDate();
        final LocalTime requestHour = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, requestDate);

        assertThat(dailyStats)
                .isNotNull()
                .hasSize(1);

        final HourlyStats hourlyStats = dailyStats.get(0);
        assertThat(hourlyStats).isNotNull();

        assertThat(hourlyStats.getRequestCount()).isEqualTo(1L);
        assertThat(hourlyStats.getInvalidCount()).isZero();
        assertThat(hourlyStats.getDate()).isEqualTo(requestDate);
        assertThat(hourlyStats.getTime()).isEqualTo(requestHour);
    }

    @Test
    void givenInvalidCustomerId_whenValidateRequest_thenNotFoundStatus() throws Exception {
        final RequestDto requestDto = new RequestDto(1L, 1L, "aaaaaaaa-bbbb-cccc-1111-222222222222",
                "123.234.56.78", 1500000000L);

        mockMvc.perform(post(validationPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error",
                        is(String.format("no customer with Id '%d' were found", requestDto.getCustomerID()))));
    }

    @Test
    void givenNullCustomerId_whenValidateRequest_thenBadRequestStatusAndDoNotCount() throws Exception {
        final RequestDto requestDto = new RequestDto(null, 1L, "aaaaaaaa-bbbb-cccc-1111-222222222222",
                "123.234.56.78", 1500000000L);

        mockMvc.perform(post(validationPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is("customerID can not be null")));
    }

    @Test
    void givenRequestWithCountableMissingFieldErrors_whenValidateRequest_thenBadRequestStatusAndIncrementInvalidCount() throws Exception {
        final Customer customer = customerService.save(new Customer(null, "name_1", true));
        final RequestDto requestDto = new RequestDto(customer.getId(), null, null,
                "123.234.56.78", null);

        mockMvc.perform(post(validationPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error.userID").value("must not be blank"))
                .andExpect(jsonPath("error.tagID").value("must not be null"))
                .andExpect(jsonPath("error.timestamp").value("must not be null"));

        //we don't have the exact timestamp stored in db we want the test work all the time (consider 23:59:59)
        //as the tests are isolated (because of @Transactional) when we fetch all hourlyStats it will just retrieve
        //the records inserted by current test.
        final List<HourlyStats> dailyStats = hourlyStatsRepository.findAll();

        assertThat(dailyStats)
                .isNotNull()
                .hasSize(1);

        final HourlyStats hourlyStats = dailyStats.get(0);
        assertThat(hourlyStats).isNotNull();

        assertThat(hourlyStats.getRequestCount()).isZero();
        assertThat(hourlyStats.getInvalidCount()).isEqualTo(1L);
    }

    @Test
    void givenMalformedRequestJson_whenValidateRequest_thenBadRequestStatusAndDoNotCount() throws Exception {
        final Customer customer = customerService.save(new Customer(null, "name_1", true));
        final String malformedJson = String.format("customerID: %d", customer.getId());
        mockMvc.perform(
                post(validationPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("MALFORMED_OR_EMPTY_REQUEST_BODY"));

        mockMvc.perform(
                post(validationPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("MALFORMED_OR_EMPTY_REQUEST_BODY"));

        //as the tests are isolated (because of @Transactional) when we fetch all hourlyStats it will just retrieve
        //the records inserted by current test.
        final List<HourlyStats> dailyStats = hourlyStatsRepository.findAll();

        assertThat(dailyStats)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void givenEmptyRequestJson_whenValidateRequest_thenBadRequestStatusAndDoNotCount() throws Exception {
        mockMvc.perform(
                post(validationPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("customerID can not be null"));
        //as the tests are isolated (because of @Transactional) when we fetch all hourlyStats it will just retrieve
        //the records inserted by current test.
        final List<HourlyStats> dailyStats = hourlyStatsRepository.findAll();

        assertThat(dailyStats)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void givenInactiveCustomer_whenValidateRequest_thenIncrementInvalidCount() throws Exception {
        final Customer customer = customerService.save(new Customer(null, "name_1", false));
        final RequestDto requestDto = new RequestDto(customer.getId(), 1L, "aaaaaaaa-bbbb-cccc-1111-222222222222",
                "123.234.56.78", 1500000000L);

        mockMvc.perform(post(validationPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error",
                        is(String.format("customer with Id '%d' is not active",customer.getId()))));

        final Instant instant = Instant.ofEpochSecond(requestDto.getTimestamp());
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        final LocalDate requestDate = zonedDateTime.toLocalDate();
        final LocalTime requestHour = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.HOURS);

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, requestDate);

        assertThat(dailyStats)
                .isNotNull()
                .hasSize(1);

        final HourlyStats hourlyStats = dailyStats.get(0);
        assertThat(hourlyStats).isNotNull();

        assertThat(hourlyStats.getRequestCount()).isZero();
        assertThat(hourlyStats.getInvalidCount()).isEqualTo(1L);
        assertThat(hourlyStats.getDate()).isEqualTo(requestDate);
        assertThat(hourlyStats.getTime()).isEqualTo(requestHour);
    }

    @Test
    void givenValidCustomerIdAndInvalidIP_whenValidateRequest_thenBadRequestStatusAndDoNotCount() throws Exception {
        final Customer customer = customerService.save(new Customer(null, "name_1", true));
        final String invalidIP = "invalidIP";
        final RequestDto requestDto = new RequestDto(customer.getId(), 1L, "aaaaaaaa-bbbb-cccc-1111-222222222222",
                invalidIP, 1500000000L);

        mockMvc.perform(post(validationPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value(String.format("'%s' is not a valid IP version 4", invalidIP)));

        final Instant instant = Instant.ofEpochSecond(requestDto.getTimestamp());
        final LocalDate requestDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, requestDate);

        assertThat(dailyStats)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void givenValidCustomerIdAndBlockedIP_whenValidateRequest_thenUnauthorizedStatusAndDoNotCount() throws Exception {
        final Customer customer = customerService.save(new Customer(null, "name_1", true));

        final Inet4Address inetAddress = (Inet4Address) InetAddress.getByName("123.234.56.78");
        ipBlackListService.addToBlackList(inetAddress);

        final RequestDto requestDto = new RequestDto(customer.getId(), 1L, "aaaaaaaa-bbbb-cccc-1111-222222222222",
                inetAddress.getHostAddress(), 1500000000L);

        mockMvc.perform(post(validationPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("error").
                        value(String.format("IP '%s' is in the black list", inetAddress.getHostAddress())));

        final Instant instant = Instant.ofEpochSecond(requestDto.getTimestamp());
        final LocalDate requestDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, requestDate);

        assertThat(dailyStats)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void givenValidCustomerIdAndBlockedRemoteAddress_whenValidateRequest_thenUnauthorizedStatusAndDoNotCount() throws Exception {
        final Customer customer = customerService.save(new Customer(null, "name_1", true));

        final Inet4Address inetAddress = (Inet4Address) InetAddress.getByName("123.234.56.78");
        ipBlackListService.addToBlackList(inetAddress);

        final RequestDto requestDto = new RequestDto(customer.getId(), 1L, "aaaaaaaa-bbbb-cccc-1111-222222222222",
                inetAddress.getHostAddress(), 1500000000L);

        mockMvc.perform(post(validationPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(request -> {
                    request.setRemoteAddr(inetAddress.getHostAddress());
                    return request;
                })
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(String.format("IP '%s' is in the black list", inetAddress.getHostAddress())));

        final Instant instant = Instant.ofEpochSecond(requestDto.getTimestamp());
        final LocalDate requestDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, requestDate);

        assertThat(dailyStats)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void givenValidCustomerIdAndBlockedUserAgent_whenValidateRequest_thenUnauthorizedStatusAndDoNotCount() throws Exception {
        final Customer customer = customerService.save(new Customer(null, "name_1", true));

        final String userAgent = "A6-Indexer";
        userAgentBlackListService.addToBlackList(userAgent);

        final RequestDto requestDto = new RequestDto(customer.getId(), 1L, "aaaaaaaa-bbbb-cccc-1111-222222222222",
                "123.234.56.78", 1500000000L);

        mockMvc.perform(post(validationPath)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(String.format("User-Agent '%s' is in the black list", userAgent)));

        final Instant instant = Instant.ofEpochSecond(requestDto.getTimestamp());
        final LocalDate requestDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        final List<HourlyStats> dailyStats = hourlyStatsService.getStatsByCustomerAndDate(customer, requestDate);

        assertThat(dailyStats)
                .isNotNull()
                .isEmpty();
    }

}
