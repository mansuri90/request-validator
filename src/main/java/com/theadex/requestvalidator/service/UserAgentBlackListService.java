package com.theadex.requestvalidator.service;

public interface UserAgentBlackListService {
    boolean isInBlackList(String userAgent);

    void addToBlackList(String userAgent);

    void removeFromBlackList(String userAgent);
}
