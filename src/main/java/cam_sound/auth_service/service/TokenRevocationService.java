package cam_sound.auth_service.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenRevocationService {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "jwt:revoked:";

    public boolean isRevoked(UUID tokenId) {
        if (tokenId == null) {
            return true;
        }

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(KEY_PREFIX + tokenId));
    }

    public void revoke(UUID tokenId, Date expiration) {
        if (tokenId == null  || expiration == null) {
            throw new IllegalArgumentException(
                    "Token ID and expiration are required");
        }

        Duration ttl = Duration.between(
                Instant.now(),
                expiration.toInstant());

        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }

        redisTemplate.opsForValue().set(
                KEY_PREFIX + tokenId,
                "revoked",
                ttl);
    }
}