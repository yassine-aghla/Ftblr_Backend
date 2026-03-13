package org.example.ftblr.Services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.Entity.*;
import org.example.ftblr.Repository.*;
import org.example.ftblr.Services.MatchParticipationService;
import org.example.ftblr.dtos.MatchParticipationDTO;
import org.example.ftblr.exception.BusinessException;
import org.example.ftblr.exception.ResourceNotFoundException;
import org.example.ftblr.mapper.MatchParticipationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MatchParticipationServiceImpl implements MatchParticipationService {

    private final MatchParticipationRepository participationRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MatchParticipationMapper participationMapper;

    @Override
    public MatchParticipationDTO joinMatch(UUID matchId, UUID teamId, UUID userId) {
        log.info("User {} joining match {} to team {}", userId, matchId, teamId);

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));

        if (!match.canJoin()) {
            throw new BusinessException("Cannot join this match. It may be full or not scheduled.");
        }

        if (participationRepository.existsByMatchIdAndUserId(matchId, userId)) {
            throw new BusinessException("User already joined this match");
        }

        if (!match.isTeamParticipating(teamId)) {
            throw new BusinessException("Team is not part of this match");
        }

        // Vérifier le nombre de joueurs dans l'équipe
        long teamPlayersCount = participationRepository.countByMatchIdAndTeamId(matchId, teamId);
        int maxPerTeam = match.getPlayersNeeded() / 2;

        if (teamPlayersCount >= maxPerTeam) {
            throw new BusinessException("This team is already full");
        }

        MatchParticipation participation = MatchParticipation.builder()
                .match(match)
                .user(user)
                .team(team)
                .build();

        MatchParticipation savedParticipation = participationRepository.save(participation);

        match.setCurrentPlayers(match.getCurrentPlayers() + 1);
        matchRepository.save(match);

        log.info("User joined match successfully");

        return participationMapper.toDTO(savedParticipation);
    }

    @Override
    public MatchParticipationDTO leaveMatch(UUID matchId, UUID userId) {
        log.info("User {} leaving match {}", userId, matchId);

        MatchParticipation participation = participationRepository.findByMatchIdAndUserId(matchId, userId)
                .orElseThrow(() -> new BusinessException("User is not participating in this match"));

        Match match = participation.getMatch();

        participationRepository.delete(participation);

        match.setCurrentPlayers(match.getCurrentPlayers() - 1);
        matchRepository.save(match);

        log.info("User left match successfully");

        return participationMapper.toDTO(participation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchParticipationDTO> getMatchParticipations(UUID matchId) {
        log.info("Fetching participations for match: {}", matchId);
        List<MatchParticipation> participations = participationRepository.findByMatchIdOrderByCreatedAtDesc(matchId);
        return participationMapper.toDTOList(participations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchParticipationDTO> getUserParticipations(UUID userId) {
        log.info("Fetching participations for user: {}", userId);
        List<MatchParticipation> participations = participationRepository.findByUserId(userId);
        return participationMapper.toDTOList(participations);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchParticipationDTO getParticipation(UUID matchId, UUID userId) {
        log.info("Fetching participation for match {} and user {}", matchId, userId);
        MatchParticipation participation = participationRepository.findByMatchIdAndUserId(matchId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation not found"));
        return participationMapper.toDTO(participation);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserInMatch(UUID matchId, UUID userId) {
        return participationRepository.existsByMatchIdAndUserId(matchId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTeamPlayersCount(UUID matchId, UUID teamId) {
        return participationRepository.countByMatchIdAndTeamId(matchId, teamId);
    }
}