package com.theadex.requestvalidator.dto.output;

import lombok.Value;

import java.time.Instant;

@Value
public class ErrorDto {
    private Instant timestamp;
	private Object error;
	private String requestInfo;
}
