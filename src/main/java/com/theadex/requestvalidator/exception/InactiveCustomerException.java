package com.theadex.requestvalidator.exception;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InactiveCustomerException extends BadRequestException {
    private final @NonNull Long customerID;

    @Override
    public String getMessage() {
        return String.format("customer with Id '%s' is not active", customerID);
    }
}
