package com.theadex.requestvalidator.validator;

import com.theadex.requestvalidator.dto.input.RequestDto;

public interface RequestValidator {
    void validate(RequestDto request);
    Order getOrder();

    /**
     * execution order of validator in ValidatorChain.
     * enum values are ordered you can change order of validation by changing the order of the enum values
     */
    enum Order {
        IP,
        CUSTOMER_ID,
        REQUIRED_FIELDS,
    }
}
