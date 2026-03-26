package org.example.ftblr.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultDetailDTO {
    private UUID matchId;
    private String matchTitle;
    private LocalDateTime matchTime;
    private String matchType;
    private String terrainName;
    private String terrainAddress;

    // Score
    private int team1Score;
    private int team2Score;
    private UUID winnerTeamId;
    private String winnerTeamName;

    // Buts
    private List<GoalDetailDTO> goals;

    // Évaluations
    private List<PlayerRatingDetailDTO> playerRatings;

    // Équipes
    private TeamDetailDTO team1;
    private TeamDetailDTO team2;
}

