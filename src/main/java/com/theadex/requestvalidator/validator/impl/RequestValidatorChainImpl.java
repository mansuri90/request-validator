package com.theadex.requestvalidator.validator.impl;

import com.theadex.requestvalidator.dto.input.RequestDto;
import com.theadex.requestvalidator.validator.RequestValidator;
import com.theadex.requestvalidator.validator.RequestValidatorChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Component
public class RequestValidatorChainImpl implements RequestValidatorChain {
    private final List<RequestValidator> validators;

    @PostConstruct
    private void sortValidators(){
        validators.sort(Comparator.comparing(RequestValidator::getOrder));
    }

    @Override
    public void validate(RequestDto request) {
        for (RequestValidator validator : validators) {
            validator.validate(request);
        }
    }
}
