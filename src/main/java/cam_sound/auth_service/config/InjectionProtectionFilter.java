package cam_sound.auth_service.config;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component

public class InjectionProtectionFilter extends OncePerRequestFilter {

    
    private static final Pattern INJECTION_PATTERN = Pattern.compile(
            "(?i)(['\";\\#]|--|\\b(select|union|insert|update|delete|drop|alter|where|and|or|from|executable)\\b|<script>)",
            Pattern.CASE_INSENSITIVE);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Inspect query string parameters
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            for (String value : entry.getValue()) {
                if (containsInjection(value)) {
                    sendBadRequestResponse(response, "Malicious input detected in parameter: " + entry.getKey());
                    return; // Block execution chain
                }
            }
        }

        // Inspect URI path segments
        String requestUri = request.getRequestURI();
        if (containsInjection(requestUri)) {
            sendBadRequestResponse(response, "Malicious patterns detected in request path.");
            return; // Block execution chain
        }

        // Proceed normally if input is safe
        filterChain.doFilter(request, response);
    }

    private boolean containsInjection(String value) {
        if (value == null)
            return false;
        return INJECTION_PATTERN.matcher(value).find();
    }

    private void sendBadRequestResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Bad Request\", \"message\": \"" + message + "\"}");
    }
}
