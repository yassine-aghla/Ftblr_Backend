package org.example.ftblr.Services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.Repository.PlayerStatsProjection;
import org.example.ftblr.Services.UserService;
import org.example.ftblr.dtos.PlayerOfMonthDTO;
import org.example.ftblr.dtos.UserDTO;
import org.example.ftblr.Entity.PositionStatus;
import org.example.ftblr.Entity.Role;
import org.example.ftblr.Entity.SkillLevel;
import org.example.ftblr.Entity.User;
import org.example.ftblr.exception.BusinessException;
import org.example.ftblr.exception.ResourceNotFoundException;
import org.example.ftblr.mapper.UserMapper;
import org.example.ftblr.Repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating new user: {}", userDTO.getEmail());

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("Email already exists: " + userDTO.getEmail());
        }

        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllActiveUsers() {
        log.info("Fetching all active users");
        List<User> users = userRepository.findByIsActiveTrue();
        return userMapper.toDTOList(users);
    }

    @Override
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        log.info("Updating user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new BusinessException("Email already exists: " + userDTO.getEmail());
            }
        }

        userMapper.updateEntityFromDTO(userDTO, existingUser);
        existingUser.setPassword(passwordEncoder.encode(existingUser.getPassword()));
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with ID: {}", id);

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        log.info("Soft deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User soft deleted successfully with ID: {}", id);
    }

    @Override
    public void hardDeleteUser(UUID id) {
        log.info("Hard deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("User hard deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(Role role) {
        log.info("Fetching users with role: {}", role);
        List<User> users = userRepository.findByRole(role);
        return userMapper.toDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersBySkillLevel(SkillLevel skillLevel) {
        log.info("Fetching users with skill level: {}", skillLevel);
        List<User> users = userRepository.findBySkillLevel(skillLevel);
        return userMapper.toDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByPosition(PositionStatus position) {
        log.info("Fetching users with position: {}", position);
        List<User> users = userRepository.findByPosition(position);
        return userMapper.toDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByCity(String city) {
        log.info("Fetching users in city: {}", city);
        List<User> users = userRepository.findByCity(city);
        return userMapper.toDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByMinimumRating(Integer minRating) {
        log.info("Fetching users with minimum rating: {}", minRating);
        List<User> users = userRepository.findByMinimumRating(minRating);
        return userMapper.toDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsers(String keyword) {
        log.info("Searching users with keyword: {}", keyword);
        List<User> users = userRepository.searchByKeyword(keyword);
        return userMapper.toDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByCityAndSkillLevel(String city, SkillLevel skillLevel) {
        log.info("Fetching users in city {} with skill level {}", city, skillLevel);
        List<User> users = userRepository.findByCityAndSkillLevel(city, skillLevel);
        return userMapper.toDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByPositionAndSkillLevel(PositionStatus position, SkillLevel skillLevel) {
        log.info("Fetching users with position {} and skill level {}", position, skillLevel);
        List<User> users = userRepository.findByPositionAndSkillLevel(position, skillLevel);
        return userMapper.toDTOList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersNearLocation(Double latitude, Double longitude, Double radiusKm) {
        log.info("Fetching users near location ({}, {}) within {} km", latitude, longitude, radiusKm);
        List<User> users = userRepository.findUsersNearLocation(latitude, longitude, radiusKm);
        return userMapper.toDTOList(users);
    }

    @Override
    public UserDTO updateUserRating(UUID userId, Integer newRating) {
        log.info("Updating rating for user {}: {}", userId, newRating);

        if (newRating < 0 || newRating > 5) {
            throw new BusinessException("Rating must be between 0 and 5");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setRating(newRating);
        User updatedUser = userRepository.save(user);
        log.info("User rating updated successfully");

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public UserDTO updateUserRole(UUID userId, Role newRole) {
        log.info("Updating role for user {}: {}", userId, newRole);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        log.info("User role updated successfully");

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public UserDTO activateUser(UUID userId) {
        log.info("Activating user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setIsActive(true);
        User updatedUser = userRepository.save(user);
        log.info("User activated successfully");

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public UserDTO deactivateUser(UUID userId) {
        log.info("Deactivating user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setIsActive(false);
        User updatedUser = userRepository.save(user);
        log.info("User deactivated successfully");

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public Double getMoyeneRating() {
        Double avg = userRepository.getMoyenneRating();
        return avg;
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        log.info("Calculating user statistics");

        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countByIsActiveTrue();
        Long admins = userRepository.countByRole(Role.ADMIN);
        Long organizateurs = userRepository.countByRole(Role.ORGANIZATEUR);
        Long joueurs = userRepository.countByRole(Role.JOUEUR);

        return UserStatistics.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(totalUsers - activeUsers)
                .admins(admins)
                .organizateurs(organizateurs)
                .joueurs(joueurs)
                .build();
    }

    @Override
    public PlayerOfMonthDTO getPlayerOfTheMonth() {
        log.info("Calculating player of the month");

        // Définir la période (mois en cours)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

        Pageable topOne = PageRequest.of(0, 1);
        List<PlayerStatsProjection> topPlayers = userRepository.findTopPlayersForPeriod(startOfMonth, endOfMonth, topOne);

        if (topPlayers.isEmpty()) {
            log.warn("No player found for the period, using default");
            return getDefaultPlayerOfMonth();
        }

        PlayerStatsProjection player = topPlayers.get(0);

        String monthName = startOfMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        int year = startOfMonth.getYear();
        String title = "Joueur du mois de " + monthName + " " + year;

        PlayerOfMonthDTO dto = new PlayerOfMonthDTO();
        dto.setId(player.getId());
        dto.setFirstName(player.getFirstName());
        dto.setLastName(player.getLastName());
        dto.setFullName(player.getFirstName() + " " + player.getLastName());
        dto.setProfilePicture(player.getProfilePicture());
        dto.setGoals(player.getGoals());
        dto.setAssists(player.getAssists());
        dto.setMatchesPlayed(player.getMatchesPlayed());
        dto.setRating(Math.round(player.getAvgRating() * 10) / 10.0);
        dto.setAttendanceRate(Math.round(player.getAttendanceRate() * 10) / 10.0);
        dto.setPosition(player.getPosition());
        dto.setTitle(title);

        return dto;
    }

    private PlayerOfMonthDTO getDefaultPlayerOfMonth() {
        PlayerOfMonthDTO dto = new PlayerOfMonthDTO();
        dto.setFirstName("Ayoub");
        dto.setLastName("Marzouk");
        dto.setFullName("Ayoub Marzouk");
        dto.setTitle("Joueur du mois par défaut");
        dto.setGoals(0);
        dto.setAssists(0);
        dto.setMatchesPlayed(0);
        dto.setRating(0);
        dto.setAttendanceRate(100);
        return dto;
    }
}
