package com.theadex.requestvalidator.config;

import com.theadex.requestvalidator.contrroller.RequestValidatorController;
import com.theadex.requestvalidator.filter.RemoteIpValidatorFilter;
import com.theadex.requestvalidator.filter.UserAgentValidatorFilter;
import com.theadex.requestvalidator.service.IpBlackListService;
import com.theadex.requestvalidator.service.UserAgentBlackListService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<UserAgentValidatorFilter> userAgentValidatorFilter(UserAgentBlackListService userAgentBlackListService) {
        FilterRegistrationBean<UserAgentValidatorFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new UserAgentValidatorFilter(userAgentBlackListService));
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RemoteIpValidatorFilter> remoteIpValidatorFilter(IpBlackListService ipBlackListService) {
        FilterRegistrationBean<RemoteIpValidatorFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RemoteIpValidatorFilter(ipBlackListService));
        registrationBean.addUrlPatterns(RequestValidatorController.REQUEST_VALIDATOR_PATH + "/*");

        return registrationBean;
    }
}
