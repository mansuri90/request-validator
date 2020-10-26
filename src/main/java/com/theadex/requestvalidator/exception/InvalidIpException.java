package com.theadex.requestvalidator.exception;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InvalidIpException extends BadRequestException {
    private final @NonNull String ip;

    @Override
    public String getMessage() {
        return String.format("'%s' is not a valid IP version 4", ip);
    }
}
