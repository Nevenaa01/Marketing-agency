package com.bsep2024.MarketingAgency.security;

import com.bsep2024.MarketingAgency.security.ratelimit.RequestWrapperFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestWrapperFilter> loggingFilter(){
        FilterRegistrationBean<RequestWrapperFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RequestWrapperFilter());
        registrationBean.addUrlPatterns("/api/ad/testRateLimiting");

        return registrationBean;
    }
}
