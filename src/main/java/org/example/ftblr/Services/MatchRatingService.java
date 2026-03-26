package org.example.ftblr.Services;

import org.example.ftblr.dtos.AttendanceDTO;
import org.example.ftblr.dtos.MatchResultDTO;
import org.example.ftblr.dtos.PlayerRatingDTO;

import java.util.List;
import java.util.UUID;

public interface MatchRatingService {

    PlayerRatingDTO ratePlayer(UUID raterId, PlayerRatingDTO ratingDTO);

    boolean hasRatedPlayer(UUID raterId, UUID ratedPlayerId, UUID matchId);

    List<PlayerRatingDTO> getMatchRatings(UUID matchId);

    List<PlayerRatingDTO> getPlayerRatings(UUID playerId);

    Double getPlayerAverageRating(UUID playerId);

    void submitMatchResult(UUID organizerId, MatchResultDTO resultDTO);

    boolean isMatchResultSubmitted(UUID matchId);


    void markAttendance(UUID matchId, List<AttendanceDTO> attendances);

    List<AttendanceDTO> getMatchAttendance(UUID matchId);

    List<UUID> getAbsentPlayers(UUID matchId);

    void banPlayer(UUID adminId, UUID playerId, String reason);

    void warnPlayer(UUID adminId, UUID playerId, String reason);
}