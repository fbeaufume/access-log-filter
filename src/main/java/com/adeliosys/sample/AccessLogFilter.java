package com.adeliosys.sample;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This web filter is used to log a technical summary at the end of each request (containing details such as the URL,
 * the response status and a duration).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10) // Use a high priority to measure the whole request
public class AccessLogFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        long duration = -System.currentTimeMillis();

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            duration += System.currentTimeMillis();

            // E.g. "GET", "POST", etc
            String httpMethod = request.getMethod();

            // E.g. "/foo/bar?acme=12"
            String url = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                url += '?' + queryString;
            }

            // E.g. 200, 404, etc
            int status = response.getStatus();

            // Not used anymore, but this is how we get the current username, then clear the thread local
            String username = UsernameExtractionFilter.getCurrentUsername();
            UsernameExtractionFilter.clearCurrentUsername();

            LOGGER.info("Served {} '{}' as {} in {} ms", httpMethod, url, status, duration);

            MDC.clear();
        }
    }
}
