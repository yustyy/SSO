package com.yusssss.sso.notificationservice.business;

import com.yusssss.sso.notificationservice.core.utilities.mail.EmailService;
import com.yusssss.sso.notificationservice.dataAccess.NotificationDao;
import com.yusssss.sso.notificationservice.dto.NotificationDto;
import com.yusssss.sso.notificationservice.entities.Notification;
import com.yusssss.sso.notificationservice.entities.NotificationChannel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class NotificationService {

    private final NotificationDao notificationDao;
    private final EmailService emailService;


    public NotificationService(NotificationDao notificationDao, EmailService emailService) {
        this.notificationDao = notificationDao;
        this.emailService = emailService;
    }


    public NotificationDto getNotificationById(UUID id) {
        var notification = notificationDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        return new NotificationDto(notification);
    }

    public List<NotificationDto> getAllNotifications() {
        return notificationDao.findAll()
                .stream()
                .map(NotificationDto::new)
                .toList();
    }

    public void sendMailNotification(String to, String subject, String body) {

        Notification notification = new Notification();
        notification.setTitle(subject);
        notification.setMessage(body);
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setSentTo(to);
        var savedNotification = notificationDao.save(notification);


        emailService.sendMail(savedNotification.getSentTo(), savedNotification.getTitle(), savedNotification.getMessage());


    }
}
