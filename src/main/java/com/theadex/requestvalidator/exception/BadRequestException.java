package com.theadex.requestvalidator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class BadRequestException extends RuntimeException {
    @Override
    public abstract String getMessage();
}
