package org.example.ftblr.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.ftblr.Entity.ParticipationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchParticipationDTO {
    private UUID id;

    @NotNull(message = "User ID is required")
    private UUID userId;
    private UserDTO user;

    @NotNull(message = "Match ID is required")
    private UUID matchId;
    private MatchDTO match;

    @NotNull(message = "Team ID is required")
    private UUID teamId;
    private TeamDTO team;

    @NotNull(message = "Status is required")
    private ParticipationStatus status;

    private Boolean isPaid;
    private LocalDateTime paymentDate;
    private String paymentReference;
    private Double amountPaid;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}