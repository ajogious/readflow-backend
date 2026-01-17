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
    private static final Set<String> ALLOWED = Set.of("WEB", "MOBILE");

    // exact public endpoints (no JWT + no X-PLATFORM required)
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/health",
            "/auth/login",
            "/auth/register",
            "/auth/verify-email",
            "/auth/forgot-password",
            "/auth/reset-password");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        boolean isAdminRoute = path.startsWith("/admin/");
        boolean isActuator = path.startsWith("/actuator/");
        boolean isPublic = isActuator || PUBLIC_PATHS.contains(path);

        if (isPublic) {
            filterChain.doFilter(request, response);
            return;
        }

        // Protected => X-PLATFORM required
        String platform = request.getHeader(HEADER);
        platform = (platform == null) ? null : platform.trim().toUpperCase();

        if (!StringUtils.hasText(platform) || !ALLOWED.contains(platform)) {
            writeJson(response, 400, path, "INVALID_REQUEST",
                    "X-PLATFORM header required (WEB or MOBILE)");
            return;
        }

        // Admin endpoints are WEB-only
        if (isAdminRoute && !"WEB".equals(platform)) {
            writeJson(response, 403, path, "PLATFORM_NOT_ALLOWED",
                    "Admin endpoints are WEB only");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeJson(HttpServletResponse response, int status, String path, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"timestamp":"%s","path":"%s","code":"%s","message":"%s"}
                """.formatted(Instant.now().toString(), path, code, message));
    }
}
