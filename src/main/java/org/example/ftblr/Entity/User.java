package org.example.ftblr.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateDeNaissance;

    @Column(nullable = false)
    private String city;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;


    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PositionStatus position;

    @Column(length = 1000)
    private String bio;

    @Column
    private String profilePicture;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (rating == null) {
            rating = 0;
        }
        if (role == null) {
            role = Role.JOUEUR;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Integer getAge() {
        if (dateDeNaissance != null) {
            return LocalDate.now().getYear() - dateDeNaissance.getYear();
        }
        return null;
    }
}