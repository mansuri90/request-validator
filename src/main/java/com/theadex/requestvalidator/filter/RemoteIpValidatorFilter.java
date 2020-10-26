package com.theadex.requestvalidator.filter;

import com.theadex.requestvalidator.service.IpBlackListService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

@Order(1)
@RequiredArgsConstructor
public class RemoteIpValidatorFilter implements Filter {
    private final @NonNull IpBlackListService ipBlackListService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        final String remoteAddress = httpServletRequest.getRemoteAddr();

        InetAddress inetAddress = InetAddress.getByName(remoteAddress);
        if (inetAddress.isAnyLocalAddress()||inetAddress.isLoopbackAddress()) {
            chain.doFilter(request, response);
        } else if (!(inetAddress instanceof Inet4Address)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(String.format("'%s' is not a valid IP version 4", remoteAddress));
        } else if (ipBlackListService.isInBlackList((Inet4Address) inetAddress)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(String.format("IP '%s' is in the black list", remoteAddress));
        } else {
            chain.doFilter(request, response);
        }
    }
}
