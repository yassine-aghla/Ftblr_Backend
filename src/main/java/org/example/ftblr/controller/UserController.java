package org.example.ftblr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.Entity.*;
import org.example.ftblr.Repository.NotificationRepository;
import org.example.ftblr.Repository.PlayerAttendanceRepository;
import org.example.ftblr.Repository.UserRepository;
import org.example.ftblr.Services.NotificationService;
import org.example.ftblr.dtos.OrganizerRequestDTO;
import org.example.ftblr.dtos.PlayerOfMonthDTO;
import org.example.ftblr.dtos.UserDTO;
import org.example.ftblr.Services.UserService;
import org.example.ftblr.exception.ResourceNotFoundException;
import org.example.ftblr.mapper.UserMapper;
import org.example.ftblr.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final NotificationRepository notificationRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PlayerAttendanceRepository attendanceRepository;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("REST request to create user: {}", userDTO.getEmail());
        UserDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        log.info("REST request to get user: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        log.info("REST request to get user by email: {}", email);
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        log.info("REST request to get all users (activeOnly: {})", activeOnly);
        List<UserDTO> users = activeOnly
                ? userService.getAllActiveUsers()
                : userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserDTO userDTO) {
        log.info("REST request to update user: {}", id);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("REST request to delete user: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable UUID id) {
        log.info("REST request to hard delete user: {}", id);
        userService.hardDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable Role role) {
        log.info("REST request to get users by role: {}", role);
        List<UserDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/skill-level/{skillLevel}")
    public ResponseEntity<List<UserDTO>> getUsersBySkillLevel(@PathVariable SkillLevel skillLevel) {
        log.info("REST request to get users by skill level: {}", skillLevel);
        List<UserDTO> users = userService.getUsersBySkillLevel(skillLevel);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<UserDTO>> getUsersByPosition(@PathVariable PositionStatus position) {
        log.info("REST request to get users by position: {}", position);
        List<UserDTO> users = userService.getUsersByPosition(position);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<UserDTO>> getUsersByCity(@PathVariable String city) {
        log.info("REST request to get users by city: {}", city);
        List<UserDTO> users = userService.getUsersByCity(city);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/filter/rating")
    public ResponseEntity<List<UserDTO>> getUsersByMinimumRating(
            @RequestParam(defaultValue = "0") Integer min) {
        log.info("REST request to get users with minimum rating: {}", min);
        List<UserDTO> users = userService.getUsersByMinimumRating(min);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String q) {
        log.info("REST request to search users with keyword: {}", q);
        List<UserDTO> users = userService.searchUsers(q);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/filter/city-skill")
    public ResponseEntity<List<UserDTO>> getUsersByCityAndSkillLevel(
            @RequestParam String city,
            @RequestParam SkillLevel skillLevel) {
        log.info("REST request to get users in {} with skill level {}", city, skillLevel);
        List<UserDTO> users = userService.getUsersByCityAndSkillLevel(city, skillLevel);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/filter/position-skill")
    public ResponseEntity<List<UserDTO>> getUsersByPositionAndSkillLevel(
            @RequestParam PositionStatus position,
            @RequestParam SkillLevel skillLevel) {
        log.info("REST request to get users with position {} and skill level {}", position, skillLevel);
        List<UserDTO> users = userService.getUsersByPositionAndSkillLevel(position, skillLevel);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<UserDTO>> getUsersNearLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        log.info("REST request to get users near ({}, {}) within {} km", latitude, longitude, radiusKm);
        List<UserDTO> users = userService.getUsersNearLocation(latitude, longitude, radiusKm);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/rating")
    public ResponseEntity<UserDTO> updateUserRating(
            @PathVariable UUID id,
            @RequestParam Integer rating) {
        log.info("REST request to update rating for user {}: {}", id, rating);
        UserDTO user = userService.updateUserRating(id, rating);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable UUID id,
            @RequestParam Role role) {
        log.info("REST request to update role for user {}: {}", id, role);
        UserDTO user = userService.updateUserRole(id, role);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable UUID id) {
        log.info("REST request to activate user: {}", id);
        UserDTO user = userService.activateUser(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable UUID id) {
        log.info("REST request to deactivate user: {}", id);
        UserDTO user = userService.deactivateUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/stats")
    public ResponseEntity<UserService.UserStatistics> getUserStatistics() {
        log.info("REST request to get user statistics");
        UserService.UserStatistics stats = userService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/Rating")
    public ResponseEntity<?>getMoyenneJoueurs(){
        Double avg=userService.getMoyeneRating();
        return ResponseEntity.ok(avg);
    }

    @PostMapping("/{id}/request-organizer")
    public ResponseEntity<?> requestOrganizerRole(@PathVariable UUID id) {
        log.info("REST request to request organizer role for user: {}", id);

        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Vous ne pouvez faire cette demande que pour vous-même"));
        }

        if (currentUser.getRole() == Role.ORGANIZATEUR || currentUser.getRole() == Role.ADMIN) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Vous avez déjà un rôle d'organisateur ou d'admin"));
        }

        String title = "Demande de rôle organisateur";
        String message = "L'utilisateur " + currentUser.getFirstName() + " " + currentUser.getLastName() +
                " (" + currentUser.getEmail() + ") souhaite devenir organisateur.";
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        admins.forEach(admin -> {
            Notification notification = Notification.builder()
                    .user(admin)
                    .title(title)
                    .message(message)
                    .type(NotificationType.ORGANIZER_REQUEST)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        });

        log.info("Organizer request sent to {} admins", admins.size());

        return ResponseEntity.ok(Map.of(
                "message", "Votre demande a été envoyée aux administrateurs",
                "success", true
        ));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("User not authenticated");
    }

    @GetMapping("/organizer-requests")
    public ResponseEntity<List<OrganizerRequestDTO>> getPendingOrganizerRequests() {
        log.info("REST request to get pending organizer requests");

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Notification> notifications = notificationRepository.findPendingOrganizerRequests();

        List<OrganizerRequestDTO> requests = notifications.stream()
                .map(notif -> {
                    OrganizerRequestDTO dto = new OrganizerRequestDTO();
                    String message = notif.getMessage();
                    String email = extractEmailFromMessage(message);

                    User requestingUser = userRepository.findByEmail(email).orElse(null);

                    if (requestingUser != null) {
                        dto.setUserId(requestingUser.getId());
                        dto.setUserFirstName(requestingUser.getFirstName());
                        dto.setUserLastName(requestingUser.getLastName());
                        dto.setUserEmail(requestingUser.getEmail());
                    } else {
                        dto.setUserId(null);
                        dto.setUserEmail(email);
                        dto.setUserFirstName(extractFirstNameFromMessage(message));
                        dto.setUserLastName(extractLastNameFromMessage(message));
                    }

                    dto.setMessage(message);
                    dto.setCreatedAt(notif.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(requests);
    }
    private String extractEmailFromMessage(String message) {
        int start = message.indexOf('(') + 1;
        int end = message.indexOf(')');
        if (start > 0 && end > start) {
            return message.substring(start, end);
        }
        return "";
    }

    private String extractFirstNameFromMessage(String message) {
        String afterUser = message.replace("L'utilisateur ", "");
        int spaceIndex = afterUser.indexOf(' ');
        if (spaceIndex > 0) {
            return afterUser.substring(0, spaceIndex);
        }
        return "Inconnu";
    }

    private String extractLastNameFromMessage(String message) {
        String afterUser = message.replace("L'utilisateur ", "");
        int firstSpace = afterUser.indexOf(' ');
        int emailStart = afterUser.indexOf('(');
        if (firstSpace > 0 && emailStart > firstSpace) {
            return afterUser.substring(firstSpace + 1, emailStart).trim();
        }
        return "Inconnu";
    }

    @PostMapping("/organizer-requests/{userId}/accept")
    public ResponseEntity<?> acceptOrganizerRequest(@PathVariable UUID userId) {
        log.info("REST request to accept organizer request for user: {}", userId);

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Seuls les administrateurs peuvent accepter les demandes"));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(Role.ORGANIZATEUR);
        userRepository.save(user);
        List<Notification> notifications = notificationRepository.findByUserIdAndType(userId, NotificationType.ORGANIZER_REQUEST);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
        Notification notif = Notification.builder()
                .user(user)
                .title(" Demande acceptée")
                .message("Félicitations ! Votre demande pour devenir organisateur a été acceptée.")
                .type(NotificationType.ORGANIZER_REQUEST_APPROVED)
                .isRead(false)
                .build();
        notificationRepository.save(notif);

        return ResponseEntity.ok(Map.of("message", "Demande acceptée avec succès"));
    }

    @PostMapping("/organizer-requests/{userId}/reject")
    public ResponseEntity<?> rejectOrganizerRequest(@PathVariable UUID userId) {
        log.info("REST request to reject organizer request for user: {}", userId);

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Seuls les administrateurs peuvent refuser les demandes"));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Notification> notifications = notificationRepository.findByUserIdAndType(userId, NotificationType.ORGANIZER_REQUEST);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);

        Notification notif = Notification.builder()
                .user(user)
                .title("Demande refusée")
                .message("Votre demande pour devenir organisateur a été refusée. Veuillez contacter un administrateur pour plus d'informations.")
                .type(NotificationType.ORGANIZER_REQUEST_REJECTED)
                .isRead(false)
                .build();
        notificationRepository.save(notif);

        return ResponseEntity.ok(Map.of("message", "Demande refusée"));
    }

    @GetMapping("/absent-players")
    public ResponseEntity<List<UserDTO>> getAbsentPlayers() {
        log.info("REST request to get absent players");
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


        List<UUID> absentPlayerIds = attendanceRepository.findDistinctPlayerIdsByStatus(AttendanceStatus.ABSENT);

        List<User> absentPlayers = userRepository.findAllById(absentPlayerIds);

        return ResponseEntity.ok(userMapper.toDTOList(absentPlayers));
    }

    @GetMapping("/{userId}/absence-count")
    public ResponseEntity<Map<String, Long>> getPlayerAbsenceCount(@PathVariable UUID userId) {
        log.info("REST request to get absence count for user: {}", userId);

        long absenceCount = attendanceRepository.countByPlayerIdAndStatus(userId, AttendanceStatus.ABSENT);

        return ResponseEntity.ok(Map.of(
                "absenceCount", absenceCount
        ));
    }

    @GetMapping("/player-of-month")
    public ResponseEntity<PlayerOfMonthDTO> getPlayerOfTheMonth() {
        log.info("REST request to get player of the month");
        PlayerOfMonthDTO player = userService.getPlayerOfTheMonth();
        return ResponseEntity.ok(player);
    }
}
