
package cam_sound.auth_service.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cam_sound.auth_service.service.jwt.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    private static final Duration WINDOW = Duration.ofMinutes(1);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getServletPath();

        int limit;

        switch (path) {
            case "/api/auth/log-in" -> limit = 5;
            case "/auth/api/otp-verify" -> limit = 5;
            case "/auth/api/register",
                    "/auth/api/forget-password" ->
                limit = 3;
            default -> {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String clientIp = request.getRemoteAddr();
        String key = "rate-limit:" + path + ":" + clientIp;

        try {
            boolean allowed = rateLimitService.allowRequest(
                    key, limit, WINDOW);

            if (!allowed) {
                response.setStatus(429);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setHeader("Retry-After", "60");
                response.getWriter().write("""
                        {
                          "status": 429,
                          "message": "Too many requests. Please try again later."
                        }
                        """);
                return;
            }

        } catch (DataAccessException e) {

            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {
                      "status": 503,
                      "message": "Authentication service temporarily unavailable."
                    }
                    """);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
