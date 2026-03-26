package org.example.ftblr.dtos;

import lombok.Data;
import org.example.ftblr.Entity.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDTO {
    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private UUID matchId;
    private String matchTitle;
    private LocalDateTime createdAt;
}