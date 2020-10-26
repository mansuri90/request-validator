package com.theadex.requestvalidator.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MissingRequiredFieldsException extends BadRequestException {
    @Getter
    private final @NonNull Map<String, String> fieldNameToErrorMessageMap;

    @Override
    public String getMessage() {
        return fieldNameToErrorMessageMap.toString();
    }
}
