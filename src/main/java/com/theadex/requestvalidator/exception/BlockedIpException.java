package com.theadex.requestvalidator.exception;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class BlockedIpException extends BadRequestException {
    private final @NonNull String ip;

    @Override
    public String getMessage() {
        return String.format("IP '%s' is in the black list", ip);
    }
}
