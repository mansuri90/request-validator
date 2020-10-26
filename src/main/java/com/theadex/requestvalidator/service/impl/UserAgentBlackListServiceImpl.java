package com.theadex.requestvalidator.service.impl;

import com.theadex.requestvalidator.domain.BlockedUserAgent;
import com.theadex.requestvalidator.repository.BlockedUserAgentRepository;
import com.theadex.requestvalidator.service.UserAgentBlackListService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAgentBlackListServiceImpl implements UserAgentBlackListService {
    private final @NonNull BlockedUserAgentRepository blockedUserAgentRepository;

    @Override
    public boolean isInBlackList(@NonNull String userAgent) {
        return blockedUserAgentRepository.findById(userAgent).isPresent();
    }

    @Override
    public void addToBlackList(@NonNull String userAgent) {
        blockedUserAgentRepository.save(new BlockedUserAgent(userAgent));
    }

    @Override
    public void removeFromBlackList(@NonNull String userAgent) {
        blockedUserAgentRepository.deleteById(userAgent);
    }
}
