package com.yusssss.sso.notificationservice.dto;

import com.yusssss.sso.notificationservice.entities.NotificationChannel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Service
public class NotificationRequest {

    @NotNull(message = "Receiver ID cannot be null")
    private UUID receiverId;

    @NotNull(message = "Title cannot be null")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @NotNull(message = "Message cannot be null")
    @Size(min = 1, message = "Message must not be empty")
    private String message;

    @NotNull(message = "Notification channel cannot be null")
    private NotificationChannel channel;

    private LocalDateTime sentAt;


}
