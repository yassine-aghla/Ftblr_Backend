package org.example.ftblr.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.ftblr.Entity.PositionStatus;
import org.example.ftblr.Entity.Role;
import org.example.ftblr.Entity.SkillLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private UUID id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDeNaissance;

    @NotBlank(message = "City is required")
    private String city;

    @Pattern(regexp = "^[0-9+\\s-]{8,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Latitude is required")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotNull(message = "Skill level is required")
    private SkillLevel skillLevel;

    @NotNull(message = "Position is required")
    private PositionStatus position;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    private String profilePicture;

    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    private Boolean isActive;

    @NotNull(message = "Role is required")
    private Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String fullName;

    private Integer age;
}
