package org.example.ftblr.Services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.Entity.*;
import org.example.ftblr.Repository.MatchRepository;
import org.example.ftblr.Repository.NotificationRepository;
import org.example.ftblr.Repository.UserRepository;
import org.example.ftblr.Services.NotificationService;
import org.example.ftblr.dtos.NotificationDTO;
import org.example.ftblr.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    @Override
    public NotificationDTO createNotification(UUID userId, String title, String message, NotificationType type, UUID matchId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Match match = null;
        if (matchId != null) {
            match = matchRepository.findById(matchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        }

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .match(match)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created for user: {}", userId);

        return mapToDTO(savedNotification);
    }

    @Override
    public List<NotificationDTO> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDTO> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }


    @Override
    public void sendMatchRatingNotifications(Match match) {
        String title = "Évaluation après match";
        String message = "Le match " + match.getTitre() + " est terminé. Veuillez évaluer les joueurs.";

        User organizer = match.getCreatedBy();

        log.info("Sending rating notification to organizer: {} (ID: {}) for match: {}",
                organizer.getEmail(), organizer.getId(), match.getTitre());


        createNotification(
                organizer.getId(),
                title,
                message,
                NotificationType.MATCH_RATING,
                match.getId()
        );

        log.info("Rating notification sent to organizer: {} for match: {}",
                organizer.getEmail(), match.getId());
    }

    @Override
    public void sendMatchResultNotification(Match match) {
        String title = "Résultat du match";
        String message = "Le match " + match.getTitre() + " s'est terminé. " +
                match.getScoreTeam1() + " - " + match.getScoreTeam2();

        match.getParticipations().forEach(participation -> {
            createNotification(
                    participation.getUser().getId(),
                    title,
                    message,
                    NotificationType.MATCH_RESULT,
                    match.getId()
            );
        });
    }

    @Override
    public void sendAbsentPlayerNotification(Match match, List<UUID> absentPlayerIds) {
        String title = "Joueurs absents signalés";
        String message = "Des joueurs ont été signalés absents pour le match " + match.getTitre() +
                ". Veuillez prendre les mesures nécessaires.";

        List<User> admins = userRepository.findByRole(Role.ADMIN);
        admins.forEach(admin -> {
            createNotification(
                    admin.getId(),
                    title,
                    message,
                    NotificationType.PLAYER_ABSENT,
                    match.getId()
            );
        });
    }

    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());

        if (notification.getMatch() != null) {
            dto.setMatchId(notification.getMatch().getId());
            dto.setMatchTitle(notification.getMatch().getTitre());
        }

        return dto;
    }
}