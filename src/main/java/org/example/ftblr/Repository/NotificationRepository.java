package org.example.ftblr.Repository;

import org.example.ftblr.Entity.Notification;
import org.example.ftblr.Entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId);

    long countByUserIdAndIsReadFalse(UUID userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") UUID userId, @Param("type") NotificationType type);
    @Query("SELECT n FROM Notification n WHERE n.type = 'ORGANIZER_REQUEST' AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findPendingOrganizerRequests();
}