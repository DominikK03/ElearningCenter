package pl.dominik.elearningcenter.infrastructure.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@elearningcenter.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String username, String token) {
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify your E-Learning Center account");
        message.setText(String.format(
                "Hello %s,\n\n" +
                        "Thank you for registering at E-Learning Center!\n\n" +
                        "Please click the link below to verify your email address:\n" +
                        "%s\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "If you didn't create an account, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "E-Learning Center Team",
                username, verificationLink
        ));

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Reset your E-Learning Center password");
        message.setText(String.format(
                "Hello %s,\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Please click the link below to reset your password:\n" +
                        "%s\n\n" +
                        "This link will expire in 1 hour.\n\n" +
                        "If you didn't request a password reset, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "E-Learning Center Team",
                username, resetLink
        ));

        mailSender.send(message);
    }

    public void sendWelcomeEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to E-Learning Center!");
        message.setText(String.format(
                "Hello %s,\n\n" +
                        "Your email has been successfully verified!\n\n" +
                        "You can now explore our courses and start learning.\n\n" +
                        "Visit: %s\n\n" +
                        "Happy learning!\n\n" +
                        "Best regards,\n" +
                        "E-Learning Center Team",
                username, frontendUrl
        ));

        mailSender.send(message);
    }

    public void sendCourseModerationEmail(
            String toEmail,
            String username,
            String courseTitle,
            String reason,
            String action
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Course " + action + " by administrator");
        message.setText(String.format(
                "Hello %s,\n\n" +
                        "Your course \"%s\" was %s by an administrator.\n\n" +
                        "Reason provided:\n%s\n\n" +
                        "If you believe this is a mistake, please contact support.\n\n" +
                        "Best regards,\n" +
                        "E-Learning Center Team",
                username,
                courseTitle,
                action,
                reason != null ? reason : "No reason provided"
        ));

        mailSender.send(message);
    }
}
