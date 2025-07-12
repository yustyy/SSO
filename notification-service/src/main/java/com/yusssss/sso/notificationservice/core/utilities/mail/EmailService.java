package com.yusssss.sso.notificationservice.core.utilities.mail;

public interface EmailService {

    boolean sendMail(String to, String subject, String body);

}
