package cam_sound.auth_service.service.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import cam_sound.auth_service.helper.CustomUserDetailsServiceHelper;
import cam_sound.auth_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.access-token.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return generateToken(
                user,
                accessTokenExpiration,
                "ACCESS");
    }

    public String generateRefreshToken(User user) {
        return generateToken(
                user,
                refreshTokenExpiration,
                "REFRESH");
    }

    private String generateToken(
            User user,
            long expiration,
            String type) {

        Date now = new Date();

        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(user.getUserId()))
                .claim("type", type)
                .claim("permission", user.getPermission().getPermission())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration));

        if ("ACCESS".equals(type)) {
            builder.claim("userId", user.getUserId());
        }

        return builder
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(
                extractAllClaims(token).getSubject());
    }

    public String extractPermission(String token) {
        return extractAllClaims(token)
                .get("permission", String.class);
    }

    public UUID extractTokenId(String token) {
        return UUID.fromString(
                extractAllClaims(token).getId());
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token)
                .get("type", String.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        if (!(userDetails instanceof CustomUserDetailsServiceHelper helper)) {
            return false;
        }

        try {
            Long tokenUserId = extractUserId(token);

            Long currentUserId = helper.getUser()
                    .getUserId()
                    .longValue();

            return tokenUserId.equals(currentUserId)
                    && "ACCESS".equals(extractTokenType(token))
                    && !isTokenExpired(token);

        } catch (io.jsonwebtoken.JwtException
                | IllegalArgumentException e) {
            return false;
        }
    }
}
