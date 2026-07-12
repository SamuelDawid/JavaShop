package org.javashop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    private static final String HEADER = "X-API-KEY";
    private static final String EXPECTED = "let-me-in";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER);
        String path = request.getRequestURI();
        if(!path.startsWith("/api/")){           // only the API is protected; static files pass freely
            filterChain.doFilter(request,response);
            return;
        }

        if(EXPECTED.equals(apiKey)) filterChain.doFilter(request,response);
        else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid API key");
        }

    }
}
