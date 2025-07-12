package com.yusssss.sso.notificationservice.dto;

import com.yusssss.sso.notificationservice.entities.Notification;
import com.yusssss.sso.notificationservice.entities.NotificationChannel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class NotificationDto {

    private UUID id;

    private String sentTo;

    private String title;

    private String message;

    private NotificationChannel channel;

    private LocalDateTime sentAt;

    public NotificationDto(Notification notification) {
        this.id = notification.getId();
        this.sentTo = notification.getSentTo();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.channel = notification.getChannel();
        this.sentAt = notification.getSentAt();
    }

}
