package cam_sound.auth_service.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import cam_sound.auth_service.utils.LocalDateTimeUtils;

@Service

public class OtpService {
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    // otp token expired at 10mn implementatiion
    public LocalDateTime getExpirationOtpTime() {
        return LocalDateTimeUtils
                .KHMER_LOCAL_DATE_TIME()
                .plusMinutes(10);
    }

    public boolean isOtpExpired(LocalDateTime expiredDateTime) {
        return LocalDateTimeUtils
                .KHMER_LOCAL_DATE_TIME()
                .isAfter(expiredDateTime);
    }
}