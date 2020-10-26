package com.theadex.requestvalidator.validator;

import com.theadex.requestvalidator.dto.input.RequestDto;

public interface RequestValidatorChain {
    void validate(RequestDto request);
}
