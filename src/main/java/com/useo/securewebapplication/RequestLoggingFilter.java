package com.useo.securewebapplication;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    // Filtering logic for logging requests
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Log request details
        logger.debug("Request URL: {}, Method: {}, Remote Address: {}",
                httpRequest.getRequestURL(), httpRequest.getMethod(), httpRequest.getRemoteAddr());

        chain.doFilter(request, response);
    }

    // Cleanup method for resources used by the filter
    @Override
    public void destroy() {
    }
}
