package org.example.ftblr.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MatchResultDTO {
    @NotNull(message = "Match ID is required")
    private UUID matchId;

    @NotNull(message = "Team 1 score is required")
    private Integer team1Score;

    @NotNull(message = "Team 2 score is required")
    private Integer team2Score;

    private UUID winnerTeamId;

    private UUID manOfMatchId;

    private List<MatchGoalDTO> goals;

    private List<AttendanceDTO> attendances;
}