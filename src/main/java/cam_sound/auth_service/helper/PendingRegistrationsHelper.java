package cam_sound.auth_service.helper;

import cam_sound.auth_service.config.PasswordConfig;
import cam_sound.auth_service.dto.request.Register;
import cam_sound.auth_service.model.PendingRegistrations;
import cam_sound.auth_service.model.User;
import cam_sound.auth_service.service.OtpService;
import cam_sound.auth_service.utils.LocalDateTimeUtils;

public final class PendingRegistrationsHelper {

    private PendingRegistrationsHelper() {

    }

    public static PendingRegistrationResult toEntity(
            Register request,
            PasswordConfig passwordConfig,
            OtpService otpService) {

        final var entity = new PendingRegistrations();
        final String otp = otpService.generateOtp();

        entity.setName(request.getName());
        entity.setUserName(request.getUserName());
        entity.setPasswordHash(
                passwordConfig.passwordEncoder()
                        .encode(request.getPassword()));
        entity.setDob(request.getDob());
        entity.setOtpHash(
                passwordConfig.passwordEncoder()
                        .encode(otp));
        entity.setOtpExpiresAt(
                otpService.getExpirationOtpTime());
        entity.setEmail(request.getEmail());
        entity.setCreateAt(
                LocalDateTimeUtils.KHMER_LOCAL_DATE_TIME());

        return new PendingRegistrationResult(entity, otp);
    }

    public static PendingRegistrationResult forgetPasswordReset(User entityUser, PasswordConfig passwordConfig,
            OtpService otpService) {
        final var entity = new PendingRegistrations();
        final String otp = otpService.generateOtp();

        entity.setName(entityUser.getName());
        entity.setUserName(entityUser.getUserName());
        entity.setPasswordHash(
                entityUser.getPassword());
        entity.setDob(entityUser.getDob());
        entity.setOtpHash(
                passwordConfig.passwordEncoder()
                        .encode(otp));
        entity.setOtpExpiresAt(
                otpService.getExpirationOtpTime());
        entity.setEmail(entityUser.getEmail());
        entity.setCreateAt(
                LocalDateTimeUtils.KHMER_LOCAL_DATE_TIME());

        return new PendingRegistrationResult(entity, otp);
    }

    public static PendingRegistrationResult forgetPasswordReset(PendingRegistrations entityPending,
            PasswordConfig passwordConfig,
            OtpService otpService) {

        final String otp = otpService.generateOtp();

        entityPending.setOtpHash(
                passwordConfig.passwordEncoder()
                        .encode(otp));

        return new PendingRegistrationResult(entityPending, otp);
    }

    public record PendingRegistrationResult(
            PendingRegistrations entity,
            String otp) {
    }
}