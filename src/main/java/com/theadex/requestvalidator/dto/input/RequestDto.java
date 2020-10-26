package com.theadex.requestvalidator.dto.input;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class RequestDto {
    @NotNull
    private Long customerID;
    @NotNull
    private Long tagID;
    @NotBlank
    private String userID;
    @NotBlank
    private String remoteIP;
    @NotNull
    private Long timestamp;
}