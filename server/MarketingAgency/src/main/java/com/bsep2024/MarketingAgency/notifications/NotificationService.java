package com.bsep2024.MarketingAgency.notifications;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    public void sendNotification(String notification) {
        messagingTemplate.convertAndSend("/topic/push-notification", notification);
    }
}
