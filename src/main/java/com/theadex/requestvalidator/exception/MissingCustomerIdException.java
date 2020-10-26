package com.theadex.requestvalidator.exception;

/**
 * uncountable
 */
public class MissingCustomerIdException extends BadRequestException {
    @Override
    public String getMessage() {
        return "customerID can not be null";
    }
}
