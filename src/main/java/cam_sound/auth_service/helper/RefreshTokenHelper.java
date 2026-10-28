package cam_sound.auth_service.helper;

import java.time.LocalDateTime;

import cam_sound.auth_service.model.RefreshToken;
import cam_sound.auth_service.utils.LocalDateTimeUtils;

public final class RefreshTokenHelper {
    private RefreshTokenHelper() {
    }

    public static RefreshToken toEntity(Integer userId, String token, LocalDateTime expiredAt) {
        final var entity = new RefreshToken();
        entity.setUserId(userId);
        entity.setToken(token);
        entity.setExpiredAt(expiredAt);
        entity.setCreateAt(LocalDateTimeUtils.KHMER_LOCAL_DATE_TIME());
        entity.setRevoked(Boolean.FALSE);
        return entity;

    }
}
