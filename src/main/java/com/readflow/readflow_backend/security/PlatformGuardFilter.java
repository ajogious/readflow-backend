package com.readflow.readflow_backend.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;

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

    private static final Set<String> ALLOWED = Set.of("WEB", "MOBILE", "POSTMAN");

    private static final Set<String> SWAGGER_PATH_PREFIXES = Set.of(
            "/v3/api-docs",
            "/swagger-ui");

    private static final Set<String> EXACT_PUBLIC_PATHS = Set.of(
            "/health",
            "/auth/login",
            "/auth/register",
            "/auth/verify-email",
            "/auth/forgot-password",
            "/auth/reset-password"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean isActuator = path.startsWith("/actuator/");
        boolean isAdminRoute = path.startsWith("/admin/");
        boolean isPublic = isPublicPath(path, method, isActuator);

        if (isPublic) {
            filterChain.doFilter(request, response);
            return;
        }

        String platform = request.getHeader(HEADER);
        platform = (platform == null) ? null : platform.trim().toUpperCase();

        if (!StringUtils.hasText(platform) || !ALLOWED.contains(platform)) {
            writeJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    path,
                    "INVALID_REQUEST",
                    "X-PLATFORM header required (WEB, MOBILE or POSTMAN)");
            return;
        }

        // Admin endpoints: WEB or POSTMAN only
        if (isAdminRoute && !Set.of("WEB", "POSTMAN").contains(platform)) {
            writeJson(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    path,
                    "PLATFORM_NOT_ALLOWED",
                    "Admin endpoints are WEB or POSTMAN only");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path, String method, boolean isActuator) {

        if (isActuator) {
            return true;
        }

        // Swagger / OpenAPI
        for (String swaggerPath : SWAGGER_PATH_PREFIXES) {
            if (path.startsWith(swaggerPath)) {
                return true;
            }
        }

        if (EXACT_PUBLIC_PATHS.contains(path)) {
            return true;
        }

        // Paystack webhook (must be public)
        if ("POST".equals(method) && path.equals("/payments/webhook")) {
            return true;
        }

        // Public read-only admin contents
        if ("GET".equals(method) && path.startsWith("/admin/contents")) {
            return true;
        }

        return false;
    }

    private void writeJson(
            HttpServletResponse response,
            int status,
            String path,
            String code,
            String message)
            throws IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {
                  "timestamp": "%s",
                  "path": "%s",
                  "code": "%s",
                  "message": "%s"
                }
                """.formatted(
                Instant.now().toString(),
                path,
                code,
                message));
    }
}
