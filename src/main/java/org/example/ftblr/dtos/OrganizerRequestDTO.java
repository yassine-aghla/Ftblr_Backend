package org.example.ftblr.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrganizerRequestDTO {
    private UUID userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String message;
    private LocalDateTime createdAt;
}