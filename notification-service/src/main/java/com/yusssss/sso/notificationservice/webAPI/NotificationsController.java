package com.yusssss.sso.notificationservice.webAPI;

import com.yusssss.sso.notificationservice.business.NotificationService;
import com.yusssss.sso.notificationservice.dto.NotificationDto;
import com.yusssss.sso.notificationservice.dto.NotificationRequest;
import com.yusssss.sso.notificationservice.core.results.DataResult;
import com.yusssss.sso.notificationservice.core.results.SuccessDataResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    private final NotificationService notificationService;

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<NotificationDto>> getNotificationById(
            @PathVariable UUID id,
            HttpServletRequest request) {

        NotificationDto notificationDto = notificationService.getNotificationById(id);

        DataResult<NotificationDto> result = new SuccessDataResult<>(
                notificationDto,
                "Notification retrieved successfully.",
                HttpStatus.OK,
                request.getRequestURI()
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/")
    public ResponseEntity<DataResult<List<NotificationDto>>> getAllNotifications(HttpServletRequest request) {
        List<NotificationDto> notifications = notificationService.getAllNotifications();

        DataResult<List<NotificationDto>> result = new SuccessDataResult<>(
                notifications,
                "Notifications retrieved successfully.",
                HttpStatus.OK,
                request.getRequestURI()
        );

        return ResponseEntity.ok(result);
    }


}