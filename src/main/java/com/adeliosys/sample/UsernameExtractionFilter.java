package com.adeliosys.sample;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This web filter extracts the current username from Spring Security for two reasons:
 * <ul>
 *     <li>Store it in a {@link ThreadLocal} and make it available to the {@link AccessLogFilter}, if we want to
 *     explicitly log the username in the {@link AccessLogFilter} log message.</li>
 *     <li>Set it in the MDC (Mapped Diagnostic Context) of Slf4J, if we want to log the username in all log
 *     messages. Note that if we use this, then logging the username explicitly in the {@link AccessLogFilter} log
 *     message is not needed anymore.</li>
 * </ul>
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

        // Not used anymore, but this is how we can store the username in the ThreadLocal
        usernameThreadLocal.set(username);

        MDC.put("user", '[' + username + "] ");

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
