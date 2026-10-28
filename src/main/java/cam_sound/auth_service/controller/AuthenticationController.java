package cam_sound.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cam_sound.auth_service.dto.request.Login;
import cam_sound.auth_service.dto.request.Otp;
import cam_sound.auth_service.dto.request.Register;
import cam_sound.auth_service.dto.request.ResetPassword;
import cam_sound.auth_service.handler.AppException;
import cam_sound.auth_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Register register) throws AppException {
        return ResponseEntity.ok().body(authService.register(register));
    }

    @PostMapping("/otp-verify")
    public ResponseEntity<?> otp(@RequestBody Otp request) throws AppException {
        return ResponseEntity.ok().body(authService.otpVerification(request));
    }

    @PostMapping("/log-in")
    public ResponseEntity<?> logIn(@RequestBody Login request, HttpServletRequest httpServletRequest)
            throws AppException {
        return ResponseEntity.ok().body(authService.Login(request, httpServletRequest));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestParam String email) throws AppException {
        return ResponseEntity.ok().body(authService.forgetPassword(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPassword resetPasswordRquest) throws AppException {
        return ResponseEntity.ok().body(authService.resetPassword(resetPasswordRquest));
    }

    @PostMapping("/log-out")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader) throws AppException {

        return ResponseEntity.ok(authService.logOut(authHeader));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok().body(authService.refreshToken(refreshToken));
    }
}
