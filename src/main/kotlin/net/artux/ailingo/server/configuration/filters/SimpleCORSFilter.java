package net.artux.ailingo.server.configuration.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
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

    private final List<String> allowedOrigins = Arrays.asList("ailingos.github.io", "*.artux.net", "http://localhost:*", "https://localhost:*");

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
            if (allowedOrigin.equals("*")) {
                return true;
            }
            if (allowedOrigin.equals(origin)) {
                return true;
            }
            if (allowedOrigin.startsWith("*.")) {
                if (origin.endsWith(allowedOrigin.substring(1))) {
                    return true;
                }
            }
            if (allowedOrigin.equals("http://localhost:*") && origin.startsWith("http://localhost:")) {
                return true;
            }
            if (allowedOrigin.equals("https://localhost:*") && origin.startsWith("https://localhost:")) {
                return true;
            }
        }
        return false;
    }


    public void init(FilterConfig filterConfig) {}

}