package cam_sound.auth_service.service.jwt;


import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT = new DefaultRedisScript<>("""
            local count = redis.call('INCR', KEYS[1])
            if count == 1 then
                redis.call('EXPIRE', KEYS[1], ARGV[1])
            end
            return count
            """, Long.class);

    public boolean allowRequest(
            String key,
            int maxRequests,
            Duration window) {

        Long count = redisTemplate.execute(
                RATE_LIMIT_SCRIPT,
                java.util.List.of(key),
                String.valueOf(window.toSeconds()));

        return count != null && count <= maxRequests;
    }
}
