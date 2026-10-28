package cam_sound.auth_service.service.serviceImpl;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cam_sound.auth_service.config.PasswordConfig;
import cam_sound.auth_service.constant.Constant;
import cam_sound.auth_service.constant.ErrorCode;
import cam_sound.auth_service.dto.GeneralResponseData;
import cam_sound.auth_service.dto.request.Login;
import cam_sound.auth_service.dto.request.Otp;
import cam_sound.auth_service.dto.request.Register;
import cam_sound.auth_service.dto.request.ResetPassword;
import cam_sound.auth_service.dto.response.LogInResponse;
import cam_sound.auth_service.dto.response.LogInResponse.InnerLogInResponse;
import cam_sound.auth_service.handler.AppException;
import cam_sound.auth_service.helper.CustomUserDetailsServiceHelper;
import cam_sound.auth_service.helper.PendingRegistrationsHelper;
import cam_sound.auth_service.helper.RefreshTokenHelper;
import cam_sound.auth_service.helper.UserHelper;
import cam_sound.auth_service.repository.PendingRegistrationsRepository;
import cam_sound.auth_service.repository.RefreshTokenRepository;
import cam_sound.auth_service.repository.UserRepository;
import cam_sound.auth_service.service.AuthService;
import cam_sound.auth_service.service.OtpService;
import cam_sound.auth_service.service.TokenRevocationService;
import cam_sound.auth_service.service.jwt.JwtService;
import cam_sound.auth_service.service.mail.OtpMailSender;
import cam_sound.auth_service.utils.IpAddressUtils;
import cam_sound.auth_service.utils.LocalDateTimeUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final GeneralResponseData generalResponseData;
    private final PendingRegistrationsRepository pendingRegistrationsRepository;
    private final PasswordConfig passwordConfig;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final OtpMailSender mailSender;
    private final TokenRevocationService tokenRevocationService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Override
    public GeneralResponseData register(Register registerRequest) {

        if (pendingRegistrationsRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AppException(
                    ErrorCode.EMAIL_REGISTER_ALREADY_AND_PENDING_FOR_VERIFY,
                    registerRequest.getEmail() + Constant.ALREADY_REGISTER_PLEASE_VERIFY);
        }
        final var entityUser = userRepository.findByEmail(registerRequest.getEmail());
        if (entityUser.isPresent()) {
            throw new AppException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    registerRequest.getEmail() + Constant.ALREADY_EXIST);
        }

        if (pendingRegistrationsRepository.existsByUserName(registerRequest.getUserName())) {
            throw new AppException(
                    ErrorCode.USERNAME_REGISTER_ALREADY_AND_PENDING_FOR_VERIFY,
                    registerRequest.getUserName() + Constant.ALREADY_REGISTER_PLEASE_VERIFY);
        }
        final var result = PendingRegistrationsHelper.toEntity(
                registerRequest,
                passwordConfig,
                otpService);

        pendingRegistrationsRepository.save(result.entity());
        mailSender.otpMailSender(result.entity().getEmail(), result.otp());
        return new GeneralResponseData();
    }

    @Override
    public GeneralResponseData otpVerification(Otp otpRequest) {

        final var pendingRegistration = pendingRegistrationsRepository
                .findByEmail(otpRequest.getEmail())
                .orElseThrow(() -> new AppException(
                        ErrorCode.INVALID_INPUT,
                        "Pending registration not found"));

        if (pendingRegistration.getOtpAttempts() >= 5) {
            throw new AppException(
                    ErrorCode.OTP_ATTEMPTS_EXCEEDED,
                    "Maximum OTP attempts exceeded");
        }

        if (otpService.isOtpExpired(
                pendingRegistration.getOtpExpiresAt())) {

            throw new AppException(
                    ErrorCode.TOKEN_EXPIRED,
                    "OTP Code Expired");
        }

        final boolean validOtp = passwordConfig
                .passwordEncoder()
                .matches(
                        otpRequest.getOtp(),
                        pendingRegistration.getOtpHash());

        if (!validOtp) {

            pendingRegistration.setOtpAttempts(
                    pendingRegistration.getOtpAttempts() + 1);

            pendingRegistrationsRepository.save(pendingRegistration);

            throw new AppException(
                    ErrorCode.INVALID_OTP,
                    "Wrong OTP Code");
        }

        final var userEntity = UserHelper.toEntity(
                pendingRegistration);

        userRepository.save(userEntity);

        pendingRegistrationsRepository.delete(
                pendingRegistration);

        return new GeneralResponseData();
    }

    @Override
    @Transactional
    public GeneralResponseData Login(Login loginRequest, HttpServletRequest httpRequest) throws AppException {
        if (loginRequest.getEmailOrUserName() == null || loginRequest.getEmailOrUserName().isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Request Gmail or UserName for sign In");
        }
        final var pendingRegistration = pendingRegistrationsRepository.findByUserNameOrEmail(
                loginRequest.getEmailOrUserName(),
                loginRequest.getEmailOrUserName());

        if (pendingRegistration.isPresent()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED_YET_PLEASE_VERIFY,
                    Constant.NOT_VERIFIED);
        }

        final var userEntity = userRepository
                .findByUserNameOrEmail(loginRequest.getEmailOrUserName(), loginRequest.getEmailOrUserName())
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMAIL_NOT_FOUND,
                        loginRequest.getEmailOrUserName() + Constant.NOT_FOUND));

        final boolean passwordVerification = passwordConfig.passwordEncoder().matches(
                loginRequest.getPassword(),
                userEntity.getPassword());

        if (!passwordVerification) {
            throw new AppException(
                    ErrorCode.INVALID_CREDENTIALS_PASSWORD,
                    "Incorrect username or password");
        }
        final var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmailOrUserName(),
                loginRequest.getPassword()));
        CustomUserDetailsServiceHelper userDetails = (CustomUserDetailsServiceHelper) authentication.getPrincipal();

        final var user = userDetails.getUser();

        String ipAddress = IpAddressUtils.getClientIp(httpRequest);

        String loginTime = LocalDateTimeUtils.KHMER_LOCAL_DATE_TIME()
                .format(
                        DateTimeFormatter.ofPattern(
                                "dd MMM yyyy, HH:mm:ss"));

        final var acessToken = jwtService.generateAccessToken(user);
        final var refreshToken = jwtService.generateRefreshToken(user);
        final var entityRefreshToken = RefreshTokenHelper.toEntity(user.getUserId(), refreshToken,
                LocalDateTimeUtils.KHMER_LOCAL_DATE_TIME().plusNanos(refreshTokenExpiration * 1_000_000));
        refreshTokenRepository.deleteAllByUserId(user.getUserId());
        final var result = refreshTokenRepository.save(entityRefreshToken);
        Object data = null;

        if (result != null) {

            try {
                mailSender.sendLoginAlert(
                        userDetails.getEmail(),
                        ipAddress,
                        loginTime);
            } catch (MessagingException ex) {
                System.getLogger(AuthServiceImpl.class.getName())
                        .log(System.Logger.Level.ERROR, "Failed to send login alert", ex);
            }

            data = LogInResponse.builder()
                    .token(new InnerLogInResponse(jwtService.extractPermission(
                            acessToken),
                            jwtService.extractUserId(
                                    acessToken).intValue(),
                            acessToken, refreshToken))
                    .tokenType(Constant.STRING_BEARER)
                    .build();
        }

        generalResponseData.setData(data);

        return generalResponseData;
    }

    @Override
    public GeneralResponseData forgetPassword(String email) throws AppException {

        final var entityUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMAIL_NOT_FOUND,
                        email + Constant.NOT_FOUND));

        final var entityPending = pendingRegistrationsRepository
                .findByEmail(email)
                .map(pending -> PendingRegistrationsHelper.forgetPasswordReset(
                        pending,
                        passwordConfig,
                        otpService))
                .orElseGet(() -> PendingRegistrationsHelper.forgetPasswordReset(
                        entityUser,
                        passwordConfig,
                        otpService));

        pendingRegistrationsRepository.save(entityPending.entity());

        mailSender.otpForgetPasswordMailSender(
                email,
                entityPending.otp());

        return new GeneralResponseData();
    }

    @Override
    public GeneralResponseData resetPassword(
            ResetPassword resetPasswordRequest) {

        final var entityUser = userRepository
                .findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMAIL_NOT_FOUND,
                        resetPasswordRequest.getEmail()
                                + Constant.NOT_FOUND));

        final var entityPending = pendingRegistrationsRepository
                .findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new AppException(
                        ErrorCode.INVALID_OTP,
                        "OTP verification request not found"));

        if (otpService.isOtpExpired(
                entityPending.getOtpExpiresAt())) {

            throw new AppException(
                    ErrorCode.EXPIRED_OTP,
                    "OTP Code Expired");
        }

        if (entityPending.getOtpAttempts() >= 5) {

            throw new AppException(
                    ErrorCode.OTP_ATTEMPTS_EXCEEDED,
                    "Maximum OTP attempts exceeded");
        }

        final boolean validOtp = passwordConfig.passwordEncoder().matches(
                resetPasswordRequest.getOtpCode(),
                entityPending.getOtpHash());

        if (!validOtp) {

            entityPending.setOtpAttempts(
                    entityPending.getOtpAttempts() + 1);

            pendingRegistrationsRepository.save(entityPending);

            throw new AppException(
                    ErrorCode.INVALID_OTP,
                    "Wrong OTP Code");
        }

        entityUser.setPassword(
                passwordConfig.passwordEncoder()
                        .encode(resetPasswordRequest.getPassword()));
        entityUser.setUpdateAt(LocalDateTimeUtils.KHMER_LOCAL_DATE_TIME());

        userRepository.save(entityUser);

        pendingRegistrationsRepository.delete(entityPending);

        return new GeneralResponseData();
    }

    @Override
    public GeneralResponseData logOut(String authHeader)
            throws AppException {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid access token");
        }

        String token = authHeader.substring(7);

        try {
            UUID tokenId = jwtService.extractTokenId(token);
            String tokenType = jwtService.extractTokenType(token);
            Date expiration = jwtService.extractExpiration(token);

            if (!"ACCESS".equals(tokenType)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid access token");
            }

            Long userId = jwtService.extractUserId(token);

            tokenRevocationService.revoke(tokenId, expiration);

            var refreshToken = refreshTokenRepository
                    .findByUserId(userId);

            if (refreshToken != null) {
                refreshToken.setRevoked(Boolean.TRUE);
                refreshTokenRepository.save(refreshToken);
            }

        } catch (ExpiredJwtException | MalformedJwtException
                | UnsupportedJwtException
                | SecurityException | IllegalArgumentException e) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid or expired access token");
        }

        return new GeneralResponseData();
    }

    @Override
    public GeneralResponseData refreshToken(String refreshToken) throws AppException {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AppException(
                    ErrorCode.INVALID_CREDENTIALS,
                    Constant.INVALID_REQUEST);
        }

        String tokenType = jwtService.extractTokenType(refreshToken);

        if (!"REFRESH".equals(tokenType)) {
            throw new AppException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Invalid refresh token type");
        }
        if (jwtService.isTokenExpired(refreshToken)) {
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new AppException(ErrorCode.TOKEN_EXPIRED, Constant.STRING_TOKEN_EXPIRED);
        }
        if (Boolean.TRUE.equals(
                refreshTokenRepository.existsByTokenAndRevokedTrue(refreshToken))) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_REVOKED, Constant.REFRESH_TOKEN_REVOKED);
        }
        final var userId = jwtService.extractUserId(refreshToken);
        final var user = userRepository.findByUserId(userId).get();
        final var newAcessToken = jwtService.generateAccessToken(user);
        Map<String, String> token = new HashMap<>();
        token.put("newAcessToken", newAcessToken);
        generalResponseData.setData(token);
        return generalResponseData;
    }

}