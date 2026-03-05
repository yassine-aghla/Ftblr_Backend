package org.example.ftblr.Services;

import org.example.ftblr.Entity.PositionStatus;
import org.example.ftblr.Entity.Role;
import org.example.ftblr.Entity.SkillLevel;
import org.example.ftblr.dtos.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(UUID id);
    UserDTO getUserByEmail(String email);
    List<UserDTO> getAllUsers();
    List<UserDTO> getAllActiveUsers();
    UserDTO updateUser(UUID id, UserDTO userDTO);
    void deleteUser(UUID id);
    void hardDeleteUser(UUID id);
    List<UserDTO> getUsersByRole(Role role);
    List<UserDTO> getUsersBySkillLevel(SkillLevel skillLevel);
    List<UserDTO> getUsersByPosition(PositionStatus position);
    List<UserDTO> getUsersByCity(String city);
    List<UserDTO> getUsersByMinimumRating(Integer minRating);
    List<UserDTO> searchUsers(String keyword);
    List<UserDTO> getUsersByCityAndSkillLevel(String city, SkillLevel skillLevel);
    List<UserDTO> getUsersByPositionAndSkillLevel(PositionStatus position, SkillLevel skillLevel);
    List<UserDTO> getUsersNearLocation(Double latitude, Double longitude, Double radiusKm);
    UserDTO updateUserRating(UUID userId, Integer newRating);
    UserDTO updateUserRole(UUID userId, Role newRole);
    UserDTO activateUser(UUID userId);
    UserDTO deactivateUser(UUID userId);
    Double getMoyeneRating();
    UserStatistics getUserStatistics();

    @lombok.Data
    @lombok.Builder
    class UserStatistics {
        private Long totalUsers;
        private Long activeUsers;
        private Long inactiveUsers;
        private Long admins;
        private Long organizateurs;
        private Long joueurs;
    }
}