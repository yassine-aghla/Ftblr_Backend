package org.example.ftblr.Services;

import org.example.ftblr.dtos.NotificationDTO;
import org.example.ftblr.Entity.NotificationType;
import org.example.ftblr.Entity.User;
import org.example.ftblr.Entity.Match;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    NotificationDTO createNotification(UUID userId, String title, String message, NotificationType type, UUID matchId);

    List<NotificationDTO> getUserNotifications(UUID userId);

    List<NotificationDTO> getUnreadNotifications(UUID userId);

    long getUnreadCount(UUID userId);

    void markAsRead(UUID notificationId);

    void markAllAsRead(UUID userId);

    void sendMatchRatingNotifications(Match match);

    void sendMatchResultNotification(Match match);

    void sendAbsentPlayerNotification(Match match, List<UUID> absentPlayerIds);
}