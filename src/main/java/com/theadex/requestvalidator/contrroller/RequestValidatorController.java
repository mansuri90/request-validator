package com.theadex.requestvalidator.contrroller;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.dto.input.RequestDto;
import com.theadex.requestvalidator.exception.InactiveCustomerException;
import com.theadex.requestvalidator.exception.MissingRequiredFieldsException;
import com.theadex.requestvalidator.service.CustomerService;
import com.theadex.requestvalidator.service.HourlyStatsService;
import com.theadex.requestvalidator.validator.RequestValidatorChain;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(RequestValidatorController.REQUEST_VALIDATOR_PATH)
@Api(tags = "Request Validator")
public class RequestValidatorController {
    private final @NonNull RequestValidatorChain validatorChain;
    private final @NonNull CustomerService customerService;
    private final @NonNull HourlyStatsService hourlyStatsService;
    public static final String REQUEST_VALIDATOR_PATH = "/request-validator";

    @ApiOperation(value = "Validates a requests and keeps the stats of valid and invalid requests")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "Request is valid"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "malformed json or missing fields or inactive customer"),
            @ApiResponse(code = SC_NOT_FOUND, message = "no customer with provided customerID found")
    })
    @PostMapping
    public void validateRequest(@RequestBody RequestDto request) {
        Instant time;
        if (request.getTimestamp() == null) {
            time = Instant.now();
        } else {
            time = Instant.ofEpochSecond(request.getTimestamp());
        }

        try {
            validatorChain.validate(request);
            final Customer customer = customerService.getCustomerById(request.getCustomerID());
            hourlyStatsService.incrementRequestCount(customer, time);
        } catch (MissingRequiredFieldsException | InactiveCustomerException ex) {
            final Customer customer = customerService.getCustomerById(request.getCustomerID());
            hourlyStatsService.incrementInvalidCount(customer, time);
            throw ex;
        }
    }
}
