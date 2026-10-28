package cam_sound.auth_service.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Constant {
    public static final String SUCESS = "SUCESS";
    public static final String ALREADY_REGISTER_PLEASE_VERIFY = " ALREADY REGISTER PLEASE VERIFY";
    public static final String ALREADY_EXIST = " ALREADY EXIST ";
    public static final String STRING_TOKEN_EXPIRED = " Token expired ";

    public static final String REQUEST_HEADER_AUTHENTICATION = "Authorization";
    public static final String STRING_BEARER = "Bearer ";

    // user sign must be have permission user
    public static final Integer USER_PERMISSION = 2;
    public static final String NOT_FOUND = " NOT FOUND";

    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    public static final String ALREADY_VERIFIED = "ALREADY_VERIFIED";
    public static final String NOT_VERIFIED = "NOT_VERIFIED";

    public static final String REFRESH_TOKEN_REVOKED = "Refresh token has been revoked.";
    public static final String PERMISSION_ADMIN = "ADMIN";
}
