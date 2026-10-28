package cam_sound.auth_service.service;

import cam_sound.auth_service.dto.GeneralResponseData;
import cam_sound.auth_service.dto.request.Login;
import cam_sound.auth_service.dto.request.Otp;
import cam_sound.auth_service.dto.request.Register;
import cam_sound.auth_service.dto.request.ResetPassword;
import cam_sound.auth_service.handler.AppException;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    GeneralResponseData register(Register registerRequest) throws AppException;

    GeneralResponseData otpVerification(Otp otpRequest) throws AppException;

    GeneralResponseData forgetPassword(String email) throws AppException;

    GeneralResponseData resetPassword(ResetPassword resetPasswordRequest);

    GeneralResponseData Login(Login loginRequest,
            HttpServletRequest httpRequest)
            throws AppException;

    GeneralResponseData logOut(String authHeader)
            throws AppException;

    GeneralResponseData refreshToken(String refreshToken)
            throws AppException;
}
