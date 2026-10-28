package cam_sound.auth_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pending_registrations")
@Data
public class PendingRegistrations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Integer registrationId;

    private String name;

    @Column(name = "user_name")
    private String userName;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private LocalDate dob;

    @Column(name = "otp_hash")
    private String otpHash;

    @Column(name = "otp_expires_at")
    private LocalDateTime otpExpiresAt;

    @Column(name = "otp_attempts")
    private Integer otpAttempts = 0;

    @Column(name = "create_at")
    private LocalDateTime createAt;
}
