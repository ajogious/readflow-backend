package com.readflow.readflow_backend.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PlatformGuardFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-PLATFORM";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only enforce for protected routes (we allow public routes without platform)
        boolean isAdminRoute = path.startsWith("/admin/");
        boolean isProtected = !(path.startsWith("/auth/") || path.equals("/health") || path.startsWith("/actuator/"));

        if (isProtected) {
            String platform = request.getHeader(HEADER);
            if (!StringUtils.hasText(platform) || !(platform.equals("WEB") || platform.equals("MOBILE"))) {
                response.setStatus(400);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter()
                        .write("""
                                {"timestamp":"","path":"%s","code":"INVALID_REQUEST","message":"X-PLATFORM header required (WEB or MOBILE)"}
                                """
                                .formatted(path));
                return;
            }

            if (isAdminRoute && !platform.equals("WEB")) {
                response.setStatus(403);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter()
                        .write("""
                                {"timestamp":"","path":"%s","code":"PLATFORM_NOT_ALLOWED","message":"Admin endpoints are WEB only"}
                                """
                                .formatted(path));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}