package com.adeliosys.sample;

import jakarta.servlet.*;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This web filter is used to extract the current username from Spring Security and make it available to the
 * access log filter.
 */
@Component
@Order(SecurityProperties.DEFAULT_FILTER_ORDER + 10) // Use a priority lower than the Spring Security filter chain
public class UsernameExtractionFilter implements Filter {

    private static final ThreadLocal<String> usernameThreadLocal = new ThreadLocal<>();

    public static String getCurrentUsername() {
        return usernameThreadLocal.get();
    }

    public static void clearCurrentUsername() {
        usernameThreadLocal.remove();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        usernameThreadLocal.set(username);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
