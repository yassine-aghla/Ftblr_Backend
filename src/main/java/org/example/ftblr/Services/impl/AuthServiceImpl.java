package org.example.ftblr.Services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.Entity.Role;
import org.example.ftblr.Entity.User;
import org.example.ftblr.Repository.UserRepository;
import org.example.ftblr.Services.AuthService;
import org.example.ftblr.dtos.auth.JwtResponse;
import org.example.ftblr.dtos.auth.LoginRequest;
import org.example.ftblr.dtos.auth.RegisterRequest;
import org.example.ftblr.exception.BusinessException;
import org.example.ftblr.security.JwtUtils;
import org.example.ftblr.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        log.info("User authenticated successfully: {}", loginRequest.getEmail());

        return new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getRole()
        );
    }

    @Override
    @Transactional
    public JwtResponse registerUser(RegisterRequest registerRequest) {
        log.info("Registering new user: {}", registerRequest.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("Email already in use!");
        }

        // Validate required fields
        validateRegisterRequest(registerRequest);

        // Create new user account with all required fields
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .dateDeNaissance(registerRequest.getDateDeNaissance())
                .city(registerRequest.getCity())
                .phoneNumber(registerRequest.getPhoneNumber()).
                profilePicture(registerRequest.getProfilePicture())
                .latitude(registerRequest.getLatitude())
                .longitude(registerRequest.getLongitude())
                .skillLevel(registerRequest.getSkillLevel())
                .position(registerRequest.getPosition())
                .bio(registerRequest.getBio() != null ? registerRequest.getBio() : "")
                .rating(0)
                .isActive(true)
                .role(Role.JOUEUR)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User saved with ID: {}", savedUser.getId());

        // Authenticate the user after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        log.info("User registered successfully: {}", registerRequest.getEmail());

        return new JwtResponse(
                jwt,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole().name()
        );
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BusinessException("Password must be at least 6 characters");
        }
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new BusinessException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new BusinessException("Last name is required");
        }
        if (request.getDateDeNaissance() == null) {
            throw new BusinessException("Date of birth is required");
        }
        if (request.getCity() == null || request.getCity().trim().isEmpty()) {
            throw new BusinessException("City is required");
        }
        if (request.getLatitude() == null) {
            throw new BusinessException("Latitude is required");
        }
        if (request.getLongitude() == null) {
            throw new BusinessException("Longitude is required");
        }
        if (request.getSkillLevel() == null) {
            throw new BusinessException("Skill level is required");
        }
        if (request.getPosition() == null) {
            throw new BusinessException("Position is required");
        }
    }
}