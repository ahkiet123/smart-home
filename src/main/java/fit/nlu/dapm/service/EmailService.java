package fit.nlu.dapm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public void sendOTP(String toEmail, String otp, String purpose) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(mailFrom);
        message.setSubject("Ma OTP " + purpose);
        message.setText("Ma OTP cho " + purpose.toLowerCase() + " cua ban la: " + otp + ". OTP co hieu luc trong 5 phut.");
        mailSender.send(message);
    }

    public void sendOTP(String toEmail, String otp) {
        sendOTP(toEmail, otp, "quen mat khau");
    }
}