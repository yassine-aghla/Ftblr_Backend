package org.example.ftblr.Services;

import org.example.ftblr.dtos.TeamDTO;
import java.util.List;
import java.util.UUID;

public interface TeamService {
    TeamDTO createTeam(TeamDTO teamDTO);
    TeamDTO getTeamById(UUID id);
    TeamDTO getTeamByName(String name);
    List<TeamDTO> getAllTeams();
    List<TeamDTO> getAllActiveTeams();
    TeamDTO updateTeam(UUID id, TeamDTO teamDTO);
    void deleteTeam(UUID id);
    void activateTeam(UUID id);
    void deactivateTeam(UUID id);
    List<TeamDTO> getTeamsByCity(String city);
    List<TeamDTO> searchTeams(String keyword);
    TeamStatistics getTeamStatistics(UUID teamId);

    @lombok.Data
    @lombok.Builder
    class TeamStatistics {
        private UUID teamId;
        private String teamName;
        private long totalMatches;
        private long wins;
        private long losses;
        private double winRate;
        private long homeMatches;
        private long awayMatches;
    }
}