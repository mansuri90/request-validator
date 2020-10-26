package com.theadex.requestvalidator.validator.impl;

import com.theadex.requestvalidator.dto.input.RequestDto;
import com.theadex.requestvalidator.exception.MissingRequiredFieldsException;
import com.theadex.requestvalidator.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RequiredFieldsValidator implements RequestValidator {
    private final Validator validator;

    @Override
    public void validate(RequestDto request) {
        final Set<ConstraintViolation<RequestDto>> constraintViolations = validator.validate(request);

        if (!constraintViolations.isEmpty()) {
            final Map<String, String> errorMap = constraintViolations.stream()
                    .collect(Collectors.toMap(cv -> cv.getPropertyPath().toString(), ConstraintViolation::getMessage));
            throw new MissingRequiredFieldsException(errorMap);
        }
    }

    @Override
    public Order getOrder() {
        return Order.REQUIRED_FIELDS;
    }
}
