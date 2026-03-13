package org.example.ftblr.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.ftblr.Entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchDTO {
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String titre;

    @NotNull(message = "Time is required")
    @Future(message = "Match time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;

    @NotNull(message = "Match type is required")
    private MatchType matchType;

    @NotNull(message = "Players needed is required")
    @Min(value = 2, message = "At least 2 players are needed")
    @Max(value = 50, message = "Maximum 50 players allowed")
    private Integer playersNeeded;

    @Min(value = 0, message = "Current players cannot be negative")
    private Integer currentPlayers;

    @NotNull(message = "Required level is required")
    private RequiredLevel requiredLevel;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost must be positive")
    private BigDecimal cout;

    @NotNull(message = "Visibility is required")
    private Visibility visibility;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private StatusMatch status;

    @NotNull(message = "Terrain ID is required")
    private UUID terrainId;
    private TerrainDTO terrain;

    @NotNull(message = "Team 1 ID is required")
    private UUID team1Id;
    private TeamDTO team1;

    @NotNull(message = "Team 2 ID is required")
    private UUID team2Id;
    private TeamDTO team2;

    private UUID winnerTeamId;
    private TeamDTO winnerTeam;

    private Integer scoreTeam1;
    private Integer scoreTeam2;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private Boolean isFull;
    private Integer remainingSlots;
    private Boolean canJoin;

    private List<MatchParticipationDTO> participations;

    // Dans la méthode enrichissement
    public List<MatchParticipationDTO> getTeam1Participations() {
        if (participations == null || team1Id == null) return List.of();
        return participations.stream()
                .filter(p -> p.getTeamId().equals(team1Id))
                .collect(Collectors.toList());
    }

    public List<MatchParticipationDTO> getTeam2Participations() {
        if (participations == null || team2Id == null) return List.of();
        return participations.stream()
                .filter(p -> p.getTeamId().equals(team2Id))
                .collect(Collectors.toList());
    }
}