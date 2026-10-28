package cam_sound.auth_service.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cam_sound.auth_service.service.CustomUserDetailsService;
import cam_sound.auth_service.service.TokenRevocationService;
import cam_sound.auth_service.service.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final TokenRevocationService tokenRevocationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        try {

            final Long userId = jwtService.extractUserId(token);
            final UUID tokenId = jwtService.extractTokenId(token);
            final String tokenType = jwtService.extractTokenType(token);

            if (tokenId == null
                    || !"ACCESS".equals(tokenType)
                    || tokenRevocationService.isRevoked(tokenId)) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                        {
                            "status": 401,
                            "message": "Access token is invalid or revoked"
                        }
                        """);
                return;
            }

            if (userId != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserById(userId);

                if (jwtService.isTokenValid(
                        token,
                        userDetails)) {

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (ExpiredJwtException e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                        "status": 401,
                        "message": "Access token expired"
                    }
                    """);

            return;

        } catch (JwtException | IllegalArgumentException e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                        "status": 401,
                        "message": "Invalid access token"
                    }
                    """);

            return;

        } catch (IOException e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                        "status": 401,
                        "message": "Authentication failed"
                    }
                    """);

            return;
        }

        filterChain.doFilter(request, response);
    }
}