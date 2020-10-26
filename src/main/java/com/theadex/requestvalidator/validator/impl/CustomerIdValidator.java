package com.theadex.requestvalidator.validator.impl;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.dto.input.RequestDto;
import com.theadex.requestvalidator.exception.MissingCustomerIdException;
import com.theadex.requestvalidator.exception.InactiveCustomerException;
import com.theadex.requestvalidator.service.CustomerService;
import com.theadex.requestvalidator.validator.RequestValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomerIdValidator implements RequestValidator {
    private final @NonNull CustomerService customerService;

    @Override
    public void validate(RequestDto request) {
        if (request.getCustomerID() == null) {
            throw new MissingCustomerIdException();
        }
        final Customer customer = customerService.getCustomerById(request.getCustomerID());
        if (!customer.isActive()) {
            throw new InactiveCustomerException(customer.getId());
        }
    }

    @Override
    public Order getOrder() {
        return Order.CUSTOMER_ID;
    }
}
