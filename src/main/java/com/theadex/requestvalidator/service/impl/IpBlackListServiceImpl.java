package com.theadex.requestvalidator.service.impl;

import com.theadex.requestvalidator.domain.BlockedIP;
import com.theadex.requestvalidator.repository.BlockedIpRepository;
import com.theadex.requestvalidator.service.IpBlackListService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;

@Service
@RequiredArgsConstructor
public class IpBlackListServiceImpl implements IpBlackListService {
    private final @NonNull BlockedIpRepository blockedIpRepository;

    @Override
    public boolean isInBlackList(@NonNull Inet4Address ip) {
        return blockedIpRepository.getIpBlackList().contains(ip);
    }

    @Override
    public void addToBlackList(@NonNull Inet4Address ip) {
        blockedIpRepository.save(new BlockedIP(null, ip));
    }

    @Override
    public void removeFromBlackList(@NonNull Inet4Address ip) {
        blockedIpRepository.deleteByIp(ip);
    }
}
