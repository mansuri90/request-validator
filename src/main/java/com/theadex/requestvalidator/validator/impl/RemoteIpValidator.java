package com.theadex.requestvalidator.validator.impl;

import com.theadex.requestvalidator.dto.input.RequestDto;
import com.theadex.requestvalidator.exception.BlockedIpException;
import com.theadex.requestvalidator.exception.InvalidIpException;
import com.theadex.requestvalidator.service.IpBlackListService;
import com.theadex.requestvalidator.validator.RequestValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@RequiredArgsConstructor
@Component
public class RemoteIpValidator implements RequestValidator {
    private final @NonNull IpBlackListService ipBlackListService;

    @Override
    public void validate(RequestDto request) {
        String remoteIP = request.getRemoteIP();
        try {
            InetAddress inetAddress = InetAddress.getByName(remoteIP);
            if (!(inetAddress instanceof Inet4Address)) {
                throw new InvalidIpException(remoteIP);
            }
            if (ipBlackListService.isInBlackList((Inet4Address) inetAddress)) {
                throw new BlockedIpException(remoteIP);
            }
        } catch (UnknownHostException e) {
            throw new InvalidIpException(remoteIP);
        }
    }

    @Override
    public Order getOrder() {
        return Order.IP;
    }
}
