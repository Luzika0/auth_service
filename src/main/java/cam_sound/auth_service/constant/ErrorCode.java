package cam_sound.auth_service.constant;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_INPUT(400, "The provided request data is invalid or malformed.", HttpStatus.BAD_REQUEST),
    EXPIRED_OTP(400, "The One-Time Password (OTP) has expired.", HttpStatus.BAD_REQUEST),
    INVALID_OTP(400, "The provided One-Time Password (OTP) is incorrect.", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED(401, "Authentication is required to access this resource.", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(401, "Incorrect username or password.", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS_PASSWORD(401, "Incorrect password.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(401, "The authentication token has expired.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(401, "The authentication token is invalid or tampered with.", HttpStatus.UNAUTHORIZED),

    OTP_ATTEMPTS_EXCEEDED(
            400,
            "The maximum number of OTP verification attempts has been exceeded.",
            HttpStatus.BAD_REQUEST),

    ACCESS_DENIED(403, "You do not have the required permissions for this action.", HttpStatus.FORBIDDEN),

    ACCOUNT_LOCKED(403, "This account has been temporarily locked due to too many failed attempts.",
            HttpStatus.FORBIDDEN),

    REFRESH_TOKEN_REVOKED(
            401,
            "Refresh token has been revoked.",
            HttpStatus.UNAUTHORIZED),

    USER_NOT_FOUND(404, "No user account was found with the provided details.", HttpStatus.NOT_FOUND),

    EMAIL_NOT_FOUND(
            404,
            "No Email or UserName was found with the provided email address.",
            HttpStatus.NOT_FOUND),

    EMAIL_REGISTER_ALREADY_AND_PENDING_FOR_VERIFY(409,
            "An account with this email address already register please verify.",
            HttpStatus.CONFLICT),
    EMAIL_NOT_VERIFIED_YET_PLEASE_VERIFY(
            403,
            "Your email address has not been verified yet. Please verify your email before logging in.",
            HttpStatus.FORBIDDEN),
    EMAIL_ALREADY_EXISTS(409, "An account with this email address already exists.", HttpStatus.CONFLICT),

    USERNAME_REGISTER_ALREADY_AND_PENDING_FOR_VERIFY(409,
            "An account with this username already register please verify.",
            HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS(409, "An account with this username already exists.", HttpStatus.CONFLICT),

    INTERNAL_SERVER_ERROR(500, "An unexpected error occurred on the server.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final Integer code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(Integer code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
