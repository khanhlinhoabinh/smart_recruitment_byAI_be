package com.fourctc.tuyendungthongminh_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Xác minh tài khoản của bạn";
        String verificationLink = "http://localhost:5173/users/verify?token=" + token;
        String message = "Chào bạn,\n\nVui lòng nhấn vào link sau để xác minh tài khoản:\n"
                + verificationLink + "\n\nLink này sẽ hết hạn sau 24 giờ.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Yêu cầu đặt lại mật khẩu");
        message.setText("Click vào link sau để đặt lại mật khẩu: " + resetLink);
        mailSender.send(message);
    }
}