package cam_sound.auth_service.helper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("authComponent")
public class AuthComponent {

    /**
     * Dynamically checks if the authenticated principal has the required role.
     * 
     * @param principal    The authentication.principal passed from @PreAuthorize
     * @param requiredRole The role to check against (e.g., 'ADMIN')
     * @return true if access is granted, false otherwise
     */
    public boolean hasRole(Object principal, String requiredRole) {
        // 1. Safe guard against unauthenticated/anonymous users
        if (principal == null || (principal instanceof String && "anonymousUser".equals(principal))) {
            return false;
        }

        // 2. Evaluate roles if using Spring Security's standard UserDetails
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> {
                        String authority = grantedAuthority.getAuthority();
                        // Matches both exact string "ADMIN" or standard Spring format "ROLE_ADMIN"
                        return authority.equals(requiredRole) || authority.equals("ROLE_" + requiredRole);
                    });
        }

        // 3. Fallback if principal is a custom class, or evaluation fails
        return false;
    }
}
