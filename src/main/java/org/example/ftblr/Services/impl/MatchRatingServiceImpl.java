package org.example.ftblr.Services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.Entity.*;
import org.example.ftblr.Repository.*;
import org.example.ftblr.Services.MatchRatingService;
import org.example.ftblr.Services.NotificationService;
import org.example.ftblr.dtos.AttendanceDTO;
import org.example.ftblr.dtos.MatchResultDTO;
import org.example.ftblr.dtos.PlayerRatingDTO;
import org.example.ftblr.exception.BusinessException;
import org.example.ftblr.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MatchRatingServiceImpl implements MatchRatingService {

    private final PlayerRatingRepository playerRatingRepository;
    private final MatchResultRepository matchResultRepository;
    private final MatchGoalRepository matchGoalRepository;
    private final PlayerAttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final NotificationService notificationService;

    @Override
    public PlayerRatingDTO ratePlayer(UUID raterId, PlayerRatingDTO ratingDTO) {
        log.info("User {} rating player {} in match {}", raterId, ratingDTO.getRatedPlayerId(), ratingDTO.getMatchId());
        User rater = userRepository.findById(raterId)
                .orElseThrow(() -> new ResourceNotFoundException("Rater not found"));

        User ratedPlayer = userRepository.findById(ratingDTO.getRatedPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Rated player not found"));

        Match match = matchRepository.findById(ratingDTO.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if (match.getStatus() != StatusMatch.COMPLETED) {
            throw new BusinessException("Cannot rate players before match is completed");
        }

        boolean isParticipant = match.getParticipations().stream()
                .anyMatch(p -> p.getUser().getId().equals(raterId));

        if (!isParticipant) {
            throw new BusinessException("Only match participants can rate players");
        }

        if (playerRatingRepository.existsByRaterIdAndRatedPlayerIdAndMatchId(raterId, ratingDTO.getRatedPlayerId(), ratingDTO.getMatchId())) {
            throw new BusinessException("You have already rated this player for this match");
        }

        PlayerRating rating = PlayerRating.builder()
                .rater(rater)
                .ratedPlayer(ratedPlayer)
                .match(match)
                .rating(ratingDTO.getRating())
                .comment(ratingDTO.getComment())
                .build();

        PlayerRating savedRating = playerRatingRepository.save(rating);

        updatePlayerAverageRating(ratedPlayer);

        log.info("Player rated successfully");

        return mapToDTO(savedRating);
    }

    private void updatePlayerAverageRating(User player) {
        Double avgRating = playerRatingRepository.getAverageRatingForPlayer(player.getId());
        if (avgRating != null) {
            player.setRating((int) Math.round(avgRating));
            userRepository.save(player);
        }
    }

    @Override
    public boolean hasRatedPlayer(UUID raterId, UUID ratedPlayerId, UUID matchId) {
        return playerRatingRepository.existsByRaterIdAndRatedPlayerIdAndMatchId(raterId, ratedPlayerId, matchId);
    }

    @Override
    public List<PlayerRatingDTO> getMatchRatings(UUID matchId) {
        return playerRatingRepository.findByMatchId(matchId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlayerRatingDTO> getPlayerRatings(UUID playerId) {
        return playerRatingRepository.findByRatedPlayerId(playerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double getPlayerAverageRating(UUID playerId) {
        return playerRatingRepository.getAverageRatingForPlayer(playerId);
    }

    @Override
    public void submitMatchResult(UUID organizerId, MatchResultDTO resultDTO) {
        log.info("Submitting result for match: {}", resultDTO.getMatchId());

        Match match = matchRepository.findById(resultDTO.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if (match.getStatus() != StatusMatch.IN_PROGRESS && match.getStatus() != StatusMatch.COMPLETED) {
            throw new BusinessException("Cannot submit result for this match");
        }

        User submitter = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!match.getCreatedBy().getId().equals(organizerId)) {
            log.warn("User {} attempted to submit result for match created by {}",
                    submitter.getEmail(), match.getCreatedBy().getEmail());
            throw new BusinessException("Seul l'organisateur qui a créé ce match peut soumettre le résultat");
        }

        if (matchResultRepository.existsByMatchId(match.getId())) {
            throw new BusinessException("Match result already submitted");
        }

        Team winnerTeam = null;
        if (resultDTO.getTeam1Score() > resultDTO.getTeam2Score()) {
            winnerTeam = match.getTeam1();
        } else if (resultDTO.getTeam2Score() > resultDTO.getTeam1Score()) {
            winnerTeam = match.getTeam2();
        }

        MatchResult matchResult = MatchResult.builder()
                .match(match)
                .team1Score(resultDTO.getTeam1Score())
                .team2Score(resultDTO.getTeam2Score())
                .winnerTeam(winnerTeam)
                .isVerified(true)
                .build();

        if (resultDTO.getManOfMatchId() != null) {
            User manOfMatch = userRepository.findById(resultDTO.getManOfMatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Man of the match not found"));
            matchResult.setManOfMatch(manOfMatch);
        }

        matchResultRepository.save(matchResult);

        if (resultDTO.getGoals() != null) {
            resultDTO.getGoals().forEach(goalDTO -> {
                User scorer = userRepository.findById(goalDTO.getScorerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Scorer not found"));

                User assistant = null;
                if (goalDTO.getAssistId() != null) {
                    assistant = userRepository.findById(goalDTO.getAssistId())
                            .orElseThrow(() -> new ResourceNotFoundException("Assistant not found"));
                }

                MatchGoal goal = MatchGoal.builder()
                        .match(match)
                        .scorer(scorer)
                        .assistant(assistant)
                        .minute(goalDTO.getMinute())
                        .teamType(goalDTO.getTeamType())
                        .build();

                matchGoalRepository.save(goal);
            });
        }

        if (resultDTO.getAttendances() != null) {
            markAttendance(match.getId(), resultDTO.getAttendances());
        }


        match.setStatus(StatusMatch.COMPLETED);
        match.setScoreTeam1(resultDTO.getTeam1Score());
        match.setScoreTeam2(resultDTO.getTeam2Score());
        match.setWinnerTeam(winnerTeam);
        matchRepository.save(match);

        notificationService.sendMatchResultNotification(match);
        notificationService.sendMatchRatingNotifications(match);

        List<UUID> absentPlayers = getAbsentPlayers(match.getId());
        if (!absentPlayers.isEmpty()) {
            notificationService.sendAbsentPlayerNotification(match, absentPlayers);
        }

        log.info("Match result submitted successfully");
    }

    @Override
    public boolean isMatchResultSubmitted(UUID matchId) {
        return matchResultRepository.existsByMatchId(matchId);
    }

    @Override
    public void markAttendance(UUID matchId, List<AttendanceDTO> attendances) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        attendances.forEach(attendanceDTO -> {
            User player = userRepository.findById(attendanceDTO.getPlayerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

            PlayerAttendance attendance = PlayerAttendance.builder()
                    .match(match)
                    .player(player)
                    .status(attendanceDTO.getStatus())
                    .build();

            attendanceRepository.save(attendance);
        });
    }

    @Override
    public List<AttendanceDTO> getMatchAttendance(UUID matchId) {
        return attendanceRepository.findByMatchId(matchId).stream()
                .map(a -> {
                    AttendanceDTO dto = new AttendanceDTO();
                    dto.setPlayerId(a.getPlayer().getId());
                    dto.setStatus(a.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UUID> getAbsentPlayers(UUID matchId) {
        return attendanceRepository.findByMatchIdAndStatus(matchId, AttendanceStatus.ABSENT).stream()
                .map(a -> a.getPlayer().getId())
                .collect(Collectors.toList());
    }

    @Override
    public void banPlayer(UUID adminId, UUID playerId, String reason) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new BusinessException("Only admins can ban players");
        }

        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        player.setIsActive(false);
        userRepository.save(player);

        notificationService.createNotification(
                playerId,
                "Compte désactivé",
                "Votre compte a été désactivé pour la raison suivante: " + reason,
                NotificationType.PLAYER_BANNED,
                null
        );

        log.info("Player {} banned by admin {}", playerId, adminId);
    }

    @Override
    public void warnPlayer(UUID adminId, UUID playerId, String reason) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new BusinessException("Only admins can warn players");
        }

        notificationService.createNotification(
                playerId,
                "Avertissement",
                "Vous avez reçu un avertissement: " + reason,
                NotificationType.PLAYER_BANNED,
                null
        );

        log.info("Player {} warned by admin {}", playerId, adminId);
    }

    private PlayerRatingDTO mapToDTO(PlayerRating rating) {
        PlayerRatingDTO dto = new PlayerRatingDTO();
        dto.setId(rating.getId());
        dto.setRatedPlayerId(rating.getRatedPlayer().getId());
        dto.setRatedPlayerName(rating.getRatedPlayer().getFullName());
        dto.setMatchId(rating.getMatch().getId());
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        return dto;
    }
}