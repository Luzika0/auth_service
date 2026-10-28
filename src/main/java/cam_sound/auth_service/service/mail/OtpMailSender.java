
package cam_sound.auth_service.service.mail;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OtpMailSender {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void otpMailSender(String email, String otp) {

        final MimeMessage message = javaMailSender.createMimeMessage();

        try {

            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromMail);
            helper.setTo(email);
            helper.setSubject("Music App - Email Verification");

            final String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport"
                              content="width=device-width, initial-scale=1.0">
                        <title>Music App Verification</title>
                    </head>

                    <body style="
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f5;
                        font-family: Arial, Helvetica, sans-serif;
                    ">

                        <div style="
                            max-width: 500px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 16px;
                            overflow: hidden;
                            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                        ">

                            <!-- Header -->
                            <div style="
                                padding: 28px;
                                text-align: center;
                                background-color: #18181b;
                                color: #ffffff;
                            ">
                                <div style="
                                    font-size: 28px;
                                    font-weight: bold;
                                ">
                                    🎵 Music App
                                </div>

                                <div style="
                                    margin-top: 8px;
                                    font-size: 14px;
                                    color: #a1a1aa;
                                ">
                                    Account Verification
                                </div>
                            </div>

                            <!-- Content -->
                            <div style="padding: 35px 30px;">

                                <h2 style="
                                    margin-top: 0;
                                    color: #18181b;
                                    font-size: 22px;
                                ">
                                    Verify your email
                                </h2>

                                <p style="
                                    color: #52525b;
                                    font-size: 15px;
                                    line-height: 1.6;
                                ">
                                    Hello,
                                </p>

                                <p style="
                                    color: #52525b;
                                    font-size: 15px;
                                    line-height: 1.6;
                                ">
                                    Use the verification code below to
                                    complete your Music App registration.
                                </p>

                                <!-- OTP -->
                                <div style="
                                    margin: 30px 0;
                                    padding: 22px;
                                    text-align: center;
                                    background-color: #f4f4f5;
                                    border-radius: 12px;
                                ">

                                    <div style="
                                        font-size: 12px;
                                        color: #71717a;
                                        margin-bottom: 10px;
                                        text-transform: uppercase;
                                        letter-spacing: 1px;
                                    ">
                                        Verification Code
                                    </div>

                                    <div style="
                                        font-size: 36px;
                                        font-weight: bold;
                                        letter-spacing: 8px;
                                        color: #18181b;
                                    ">
                                        %s
                                    </div>

                                </div>

                                <p style="
                                    color: #71717a;
                                    font-size: 14px;
                                    line-height: 1.6;
                                ">
                                    This verification code will expire in
                                    <strong>10 minutes</strong>.
                                </p>

                                <p style="
                                    color: #71717a;
                                    font-size: 14px;
                                    line-height: 1.6;
                                ">
                                    If you did not request this verification
                                    code, you can safely ignore this email.
                                </p>

                            </div>

                            <!-- Footer -->
                            <div style="
                                padding: 20px 30px;
                                text-align: center;
                                background-color: #fafafa;
                                border-top: 1px solid #e4e4e7;
                            ">
                                <p style="
                                    margin: 0;
                                    color: #a1a1aa;
                                    font-size: 12px;
                                ">
                                    © 2026 Music App. All rights reserved.
                                </p>
                            </div>

                        </div>

                    </body>
                    </html>
                    """.formatted(otp);

            helper.setText(htmlContent, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void otpForgetPasswordMailSender(String email, String otp) {
        final MimeMessage message = javaMailSender.createMimeMessage();

        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromMail);
            helper.setTo(email);
            helper.setSubject("Music App - Password Reset Verification");

            final String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Password Reset</title>
                    </head>
                    <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: Arial, Helvetica, sans-serif;">
                        <div style="max-width: 500px; margin: 40px auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                            <div style="padding: 28px; text-align: center; background-color: #18181b; color: #ffffff;">
                                <div style="font-size: 28px; font-weight: bold;">🎵 Music App</div>
                                <div style="margin-top: 8px; font-size: 14px; color: #a1a1aa;">Password Reset</div>
                            </div>
                            <div style="padding: 35px 30px;">
                                <h2 style="margin-top: 0; color: #18181b; font-size: 22px;">Reset your password</h2>
                                <p style="color: #52525b; font-size: 15px; line-height: 1.6;">Hello,</p>
                                <p style="color: #52525b; font-size: 15px; line-height: 1.6;">We received a request to reset the password for your Music App account.</p>
                                <p style="color: #52525b; font-size: 15px; line-height: 1.6;">Use the verification code below to continue resetting your password.</p>
                                <!-- OTP -->
                                <div style="margin: 30px 0; padding: 22px; text-align: center; background-color: #f4f4f5; border-radius: 12px;">
                                    <div style="font-size: 12px; color: #71717a; margin-bottom: 10px; text-transform: uppercase; letter-spacing: 1px;">Verification Code</div>
                                    <div style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #18181b;">%s</div>
                                </div>
                                <p style="color: #71717a; font-size: 14px; line-height: 1.6;">This verification code will expire in <strong>10 minutes</strong>.</p>
                                <p style="color: #71717a; font-size: 14px; line-height: 1.6;">For your security, never share this code with anyone.</p>
                                <p style="color: #71717a; font-size: 14px; line-height: 1.6;">If you did not request a password reset, please ignore this email. Your password will not be changed.</p>
                            </div>
                            <div style="padding: 20px 30px; text-align: center; background-color: #fafafa; border-top: 1px solid #e4e4e7;">
                                <p style="margin: 0; color: #a1a1aa; font-size: 12px;">© 2026 Music App. All rights reserved.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """
                    .formatted(otp);

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset OTP email", e);
        }
    }

    public void sendLoginAlert(
            String email,
            String ipAddress,
            String loginTime) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                true,
                StandardCharsets.UTF_8.name());

        helper.setTo(email);

        helper.setSubject(
                "Music App - New Login Detected");

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>New Login Detected</title>
                </head>

                <body style="
                    margin: 0;
                    padding: 0;
                    background-color: #f4f6f8;
                    font-family: Arial, sans-serif;
                ">

                    <div style="
                        max-width: 600px;
                        margin: 40px auto;
                        background: #ffffff;
                        border-radius: 12px;
                        overflow: hidden;
                        box-shadow: 0 4px 15px rgba(0,0,0,0.08);
                    ">

                        <!-- Header -->
                        <div style="
                            background: #111827;
                            padding: 25px;
                            text-align: center;
                            color: white;
                        ">

                            <h1 style="
                                margin: 0;
                                font-size: 24px;
                            ">
                                Music App
                            </h1>

                            <p style="
                                margin: 8px 0 0;
                                color: #d1d5db;
                            ">
                                Security Alert
                            </p>

                        </div>

                        <!-- Content -->
                        <div style="
                            padding: 30px;
                        ">

                            <h2 style="
                                color: #111827;
                                margin-top: 0;
                            ">
                                New login detected
                            </h2>

                            <p style="
                                color: #4b5563;
                                line-height: 1.6;
                            ">
                                We detected a new login to your
                                Music App account.
                            </p>

                            <!-- Login Information -->
                            <div style="
                                margin-top: 25px;
                                background: #f9fafb;
                                border: 1px solid #e5e7eb;
                                border-radius: 8px;
                                padding: 20px;
                            ">

                                <p style="margin: 8px 0;">
                                    <strong>Account:</strong>
                                    %s
                                </p>

                                <p style="margin: 8px 0;">
                                    <strong>IP Address:</strong>
                                    %s
                                </p>

                                <p style="margin: 8px 0;">
                                    <strong>Login Time:</strong>
                                    %s
                                </p>

                            </div>

                            <!-- Warning -->
                            <div style="
                                margin-top: 25px;
                                padding: 15px;
                                background: #fff7ed;
                                border-left: 4px solid #f97316;
                                color: #9a3412;
                            ">

                                <strong>
                                    Wasn't you?
                                </strong>

                                <p style="
                                    margin-bottom: 0;
                                ">
                                    Please change your password
                                    immediately and secure your account.
                                </p>

                            </div>

                            <p style="
                                margin-top: 30px;
                                color: #6b7280;
                                font-size: 14px;
                                line-height: 1.6;
                            ">
                                If you recognize this activity,
                                no action is required.
                            </p>

                        </div>

                        <!-- Footer -->
                        <div style="
                            background: #f9fafb;
                            padding: 20px;
                            text-align: center;
                            color: #9ca3af;
                            font-size: 12px;
                        ">

                            This is an automated security notification.
                            <br>
                            Please do not reply to this email.

                        </div>

                    </div>

                </body>
                </html>
                """.formatted(
                email,
                ipAddress,
                loginTime);

        helper.setText(html, true);

        javaMailSender.send(message);
    }

}
