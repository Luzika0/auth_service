package cam_sound.auth_service.helper;

import cam_sound.auth_service.constant.Constant;
import cam_sound.auth_service.model.PendingRegistrations;
import cam_sound.auth_service.model.User;
import cam_sound.auth_service.utils.LocalDateTimeUtils;

public final class UserHelper {
    private UserHelper() {

    }

    public static User toEntity(PendingRegistrations registerRequest) {
        final var entity = new User();
        entity.setName(registerRequest.getName());
        entity.setUserName(registerRequest.getUserName());
        entity.setEmail(registerRequest.getEmail());
        entity.setPermissionId(Constant.USER_PERMISSION);
        entity.setPassword(registerRequest.getPasswordHash());
        entity.setCreateAt(LocalDateTimeUtils.KHMER_LOCAL_DATE_TIME());
        entity.setDob(registerRequest.getDob());
        entity.setIsUserEnable(true);
        return entity;
    }
}
