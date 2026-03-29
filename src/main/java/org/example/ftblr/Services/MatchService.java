package org.example.ftblr.Services;

import org.example.ftblr.dtos.MatchDTO;
import org.example.ftblr.Entity.*;
import org.example.ftblr.dtos.MatchResultDetailDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MatchService {
    MatchDTO createMatch(MatchDTO matchDTO);
    MatchDTO getMatchById(UUID id);
    List<MatchDTO> getAllMatches();
    MatchDTO updateMatch(UUID id, MatchDTO matchDTO);
    void deleteMatch(UUID id);

    List<MatchDTO> getMatchesByStatus(StatusMatch status);
    List<MatchDTO> getMatchesByType(MatchType matchType);
    List<MatchDTO> getMatchesByLevel(RequiredLevel requiredLevel);
    List<MatchDTO> getMatchesByVisibility(Visibility visibility);
    List<MatchDTO> getMatchesByTerrain(UUID terrainId);
    List<MatchDTO> getMatchesByTeam(UUID teamId);
    boolean isTerrainAvailable(UUID terrainId, LocalDateTime time, UUID excludeMatchId);


    List<MatchDTO> getUpcomingMatches();
    List<MatchDTO> getPastMatches();
    List<MatchDTO> getTodayMatches();
    List<MatchDTO> getMatchesBetweenDates(LocalDateTime start, LocalDateTime end);
    List<MatchDTO> getMatchesByTerrainAndDateRange(UUID terrainId, LocalDateTime start, LocalDateTime end);

    List<MatchDTO> getAvailableMatches();
    List<MatchDTO> getFullMatches();
    List<MatchDTO> searchMatches(String keyword);

    MatchDTO joinMatch(UUID matchId);
    MatchDTO leaveMatch(UUID matchId);
    MatchDTO cancelMatch(UUID matchId);
    MatchDTO startMatch(UUID matchId);
    MatchDTO completeMatch(UUID matchId, UUID winnerTeamId, Integer score1, Integer score2);
    MatchDTO postponeMatch(UUID matchId, LocalDateTime newTime);

    long countMatchesByStatus(StatusMatch status);
    long countMatchesByTerrain(UUID terrainId);
    MatchStatistics getMatchStatistics();
    List<MatchDTO> getHeadToHead(UUID team1Id, UUID team2Id);
    MatchResultDetailDTO getMatchResultDetail(UUID matchId);

    @lombok.Data
    @lombok.Builder
    class MatchStatistics {
        private long totalMatches;
        private long scheduled;
        private long inProgress;
        private long completed;
        private long cancelled;
        private long postponed;
        private double averagePlayersPerMatch;
        private MatchDTO mostAnticipated;
        private MatchDTO highestScoring;
    }
}