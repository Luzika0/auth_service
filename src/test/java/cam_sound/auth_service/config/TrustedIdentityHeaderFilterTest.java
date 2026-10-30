package cam_sound.auth_service.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import cam_sound.auth_service.helper.CustomUserDetailsServiceHelper;
import cam_sound.auth_service.model.User;
import jakarta.servlet.http.HttpServletRequest;

class TrustedIdentityHeaderFilterTest {

    private final TrustedIdentityHeaderFilter filter = new TrustedIdentityHeaderFilter();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void removesClientSuppliedIdentityWhenRequestIsAnonymous() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "999");

        filter.doFilter(request, new MockHttpServletResponse(), (wrapped, response) -> {
            HttpServletRequest forwarded = (HttpServletRequest) wrapped;
            assertNull(forwarded.getHeader("X-User-Id"));
            assertFalse(Collections.list(forwarded.getHeaderNames()).stream()
                    .anyMatch(name -> name.equalsIgnoreCase("X-User-Id")));
        });
    }

    @Test
    void replacesClientSuppliedIdentityWithAuthenticatedPrincipal() throws Exception {
        User user = new User();
        user.setUserId(27);
        var principal = new CustomUserDetailsServiceHelper(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "999");

        filter.doFilter(request, new MockHttpServletResponse(), (wrapped, response) -> {
            HttpServletRequest forwarded = (HttpServletRequest) wrapped;
            assertEquals("27", forwarded.getHeader("X-User-Id"));
            assertEquals(List.of("27"), Collections.list(forwarded.getHeaders("X-User-Id")));
        });
    }
}
