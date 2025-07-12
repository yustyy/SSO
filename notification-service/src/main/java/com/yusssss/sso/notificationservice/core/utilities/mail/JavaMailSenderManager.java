package com.yusssss.sso.notificationservice.core.utilities.mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;


@Service
public class JavaMailSenderManager implements EmailService{

    private final JavaMailSender javaMailsender;

    public JavaMailSenderManager(JavaMailSender javaMailsender) {
        this.javaMailsender = javaMailsender;
    }

    @Value("${spring.mail.username}")
    private String from;


    @Override
    public boolean sendMail(String to, String subject, String body) {

        MimeMessage mimeMessage = javaMailsender.createMimeMessage();

        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailsender.send(mimeMessage);
            return true;
        }catch (Exception e){
           throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }


    }

}
