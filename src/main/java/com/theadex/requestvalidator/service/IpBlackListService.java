package com.theadex.requestvalidator.service;

import java.net.Inet4Address;

public interface IpBlackListService {
    boolean isInBlackList(Inet4Address ip);
    void addToBlackList(Inet4Address ip);
    void removeFromBlackList(Inet4Address ip);
}
