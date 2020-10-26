package com.theadex.requestvalidator.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends BadRequestException {
    private final Long customerID;

    @Override
    public String getMessage() {
        return String.format("no customer with Id '%s' were found", customerID);
    }
}
