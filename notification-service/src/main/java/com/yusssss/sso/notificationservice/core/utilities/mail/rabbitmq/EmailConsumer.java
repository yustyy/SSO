package com.yusssss.sso.notificationservice.core.utilities.mail.rabbitmq;

import com.yusssss.sso.notificationservice.business.NotificationService;
import com.yusssss.sso.notificationservice.core.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private final NotificationService notificationService;

    public EmailConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void listenEmailQueue(@Payload EmailMessage emailMessage) {
        notificationService.sendMailNotification(emailMessage.getTo(), emailMessage.getSubject(), emailMessage.getBody());
        System.out.println("Email sent to: " + emailMessage.getTo());
    }
}
