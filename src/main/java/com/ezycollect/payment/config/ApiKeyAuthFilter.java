package com.ezycollect.payment.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    @Value("${api.key}")
    private String apiKey;
    private static final String API_KEY_HEADER = "X-API-KEY";

    /**
     * Filter to authenticate requests based on API key in header
     * If the API key matches, set authentication in context
     * Otherwise, respond with 401 Unauthorized
     * Using API key for simplicity; JWT or OAuth2 can be used for more robust security in prod scenaios
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().startsWith("/v1/")) {
            log.info("Authenticating Request");
            String requestApiKey = request.getHeader(API_KEY_HEADER);
            if (requestApiKey != null && apiKey.equals(requestApiKey.trim())) {
                log.info("Authentication Successful");
                // Set authentication in context
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "apikey-user", null, Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
            } else {
                log.info("Unauthorized Request - Invalid API Key");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized: Invalid API Key\"}");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
