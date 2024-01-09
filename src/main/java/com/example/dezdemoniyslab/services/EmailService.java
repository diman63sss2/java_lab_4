package com.example.dezdemoniyslab.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private static final Logger logger = LoggerFactory
            .getLogger(EmailService.class);

    @Value("admin.email")
    public static String adminEmail;


    public void sendTextEmail(String destination, String subject, String message ) {
        logger.info("Simple Email sending start");

        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo(destination);
        simpleMessage.setSubject(subject);
        simpleMessage.setText(message);

        javaMailSender.send(simpleMessage);

        logger.info("Simple Email sent");

    }
}
