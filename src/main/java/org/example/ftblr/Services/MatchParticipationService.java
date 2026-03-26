package org.example.ftblr.Services;

import org.example.ftblr.dtos.MatchParticipationDTO;

import java.util.List;
import java.util.UUID;

public interface MatchParticipationService {
    MatchParticipationDTO joinMatch(UUID matchId, UUID teamId, UUID userId);
    MatchParticipationDTO leaveMatch(UUID matchId, UUID userId);
    List<MatchParticipationDTO> getMatchParticipations(UUID matchId);
    List<MatchParticipationDTO> getUserParticipations(UUID userId);
    MatchParticipationDTO getParticipation(UUID matchId, UUID userId);
    boolean isUserInMatch(UUID matchId, UUID userId);
    long getTeamPlayersCount(UUID matchId, UUID teamId);
}