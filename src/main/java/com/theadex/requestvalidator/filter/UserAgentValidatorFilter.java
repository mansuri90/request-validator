package com.theadex.requestvalidator.filter;

import com.theadex.requestvalidator.service.UserAgentBlackListService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(2)
@RequiredArgsConstructor
public class UserAgentValidatorFilter implements Filter {
    private final @NonNull UserAgentBlackListService userAgentBlackListService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        final String userAgent = httpServletRequest.getHeader(HttpHeaders.USER_AGENT);
        if (userAgent != null && userAgentBlackListService.isInBlackList(userAgent)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(String.format("User-Agent '%s' is in the black list", userAgent));
        } else {
            chain.doFilter(request, response);
        }
    }

}
