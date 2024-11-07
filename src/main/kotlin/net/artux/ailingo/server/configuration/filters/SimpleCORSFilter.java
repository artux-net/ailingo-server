package net.artux.ailingo.server.configuration.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCORSFilter implements Filter {

    private final List<String> allowedOrigins = Arrays.asList("ailingos.github.io", "*.artux.net", "localhost");

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");

        if (origin != null && isAllowedOrigin(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");

        chain.doFilter(req, res);
    }

    private boolean isAllowedOrigin(String origin) {
        for (String allowedOrigin : allowedOrigins) {
            if (allowedOrigin.equals("*") || allowedOrigin.equals(origin) || (allowedOrigin.startsWith("*.") && origin.endsWith(allowedOrigin.substring(1)))) {
                return true;
            }
        }
        return false;
    }


    public void init(FilterConfig filterConfig) {}

}