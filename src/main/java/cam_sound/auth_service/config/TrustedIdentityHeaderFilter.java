package cam_sound.auth_service.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import cam_sound.auth_service.helper.CustomUserDetailsServiceHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Removes any client-supplied X-User-Id and exposes only the user ID from the
 * principal authenticated by JwtFilter. Gateway MVC forwards this wrapped
 * request to Music Service.
 */
public class TrustedIdentityHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String trustedUserId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getPrincipal() instanceof CustomUserDetailsServiceHelper principal) {
            trustedUserId = String.valueOf(principal.getUserId());
        }

        filterChain.doFilter(new TrustedIdentityRequest(request, trustedUserId), response);
    }

    private static final class TrustedIdentityRequest extends HttpServletRequestWrapper {
        private final String trustedUserId;

        private TrustedIdentityRequest(HttpServletRequest request, String trustedUserId) {
            super(request);
            this.trustedUserId = trustedUserId;
        }

        @Override
        public String getHeader(String name) {
            if ("X-User-Id".equalsIgnoreCase(name)) {
                return trustedUserId;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if ("X-User-Id".equalsIgnoreCase(name)) {
                return trustedUserId == null
                        ? Collections.emptyEnumeration()
                        : Collections.enumeration(List.of(trustedUserId));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = new ArrayList<>();
            Enumeration<String> original = super.getHeaderNames();
            while (original != null && original.hasMoreElements()) {
                String name = original.nextElement();
                if (!"X-User-Id".equalsIgnoreCase(name)) {
                    names.add(name);
                }
            }
            if (trustedUserId != null) {
                names.add("X-User-Id");
            }
            return Collections.enumeration(names);
        }
    }
}
