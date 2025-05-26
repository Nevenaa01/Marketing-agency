package com.bsep2024.MarketingAgency.security.ratelimit;

import com.bsep2024.MarketingAgency.utils.RequestWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
        System.out.println("Request Body: " + requestWrapper.getBody());

        chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {
    }
}
