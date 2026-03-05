package org.example.ftblr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.dtos.UserDTO;
import org.example.ftblr.Entity.PositionStatus;
import org.example.ftblr.Entity.Role;
import org.example.ftblr.Entity.SkillLevel;
import org.example.ftblr.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

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
}
