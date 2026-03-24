package org.example.ftblr.Services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.Repository.UserRepository;
import org.example.ftblr.Services.NotificationService;
import org.example.ftblr.dtos.MatchDTO;
import org.example.ftblr.Entity.*;
import org.example.ftblr.dtos.MatchParticipationDTO;
import org.example.ftblr.dtos.UserDTO;
import org.example.ftblr.exception.BusinessException;
import org.example.ftblr.exception.ResourceNotFoundException;
import org.example.ftblr.mapper.MatchMapper;
import org.example.ftblr.Repository.MatchRepository;
import org.example.ftblr.Repository.TeamRepository;
import org.example.ftblr.Repository.TerrainRepository;
import org.example.ftblr.Services.MatchService;
import org.example.ftblr.mapper.TeamMapper;
import org.example.ftblr.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TerrainRepository terrainRepository;
    private final TeamRepository teamRepository;
    private final MatchMapper matchMapper;
    private final TeamMapper teamMapper;
    private final UserRepository userRepository;

    @Override
    public MatchDTO createMatch(MatchDTO matchDTO) {
        log.info("Creating new match: {}", matchDTO.getTitre());

        User currentUser = getCurrentUser();
        
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.ORGANIZATEUR) {
            log.warn("User {} with role {} attempted to create a match without permission",
                    currentUser.getEmail(), currentUser.getRole());
            throw new BusinessException("Seuls les administrateurs et les organisateurs peuvent créer des matchs. " +
                    "Veuillez contacter un administrateur pour devenir organisateur.");
        }

        Terrain terrain = validateTerrain(matchDTO.getTerrainId());
        Team team1 = validateTeam(matchDTO.getTeam1Id());
        Team team2 = validateTeam(matchDTO.getTeam2Id());

        validateTeamsAreDifferent(team1, team2);
        validateTeamAvailability(team1.getId(), matchDTO.getTime(), null);
        validateTeamAvailability(team2.getId(), matchDTO.getTime(), null);
        validateTerrainAvailability(terrain.getId(), matchDTO.getTime(), null);
        validateNoDuplicateMatch(team1.getId(), team2.getId(), matchDTO.getTime(), null);

        Match match = matchMapper.toEntity(matchDTO);
        match.setTerrain(terrain);
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setCreatedBy(currentUser);

        Match savedMatch = matchRepository.save(match);
        log.info("Match created successfully with ID: {}", savedMatch.getId());

        return matchMapper.toDTO(savedMatch);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new BusinessException("User not found"));
        }
        throw new BusinessException("User not authenticated");
    }

    @Override
    @Transactional(readOnly = true)
    public MatchDTO getMatchById(UUID id) {
        log.info("Fetching match with ID: {}", id);

        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + id));

        if (match.getParticipations() != null) {
            match.getParticipations().size();
        }

        MatchDTO matchDTO = matchMapper.toDTO(match);

        if (match.getParticipations() != null) {
            List<MatchParticipationDTO> participationDTOs = match.getParticipations().stream()
                    .map(p -> {
                        MatchParticipationDTO dto = new MatchParticipationDTO();
                        dto.setId(p.getId());
                        dto.setUserId(p.getUser().getId());
                        UserDTO userSimple = new UserDTO();
                        userSimple.setId(p.getUser().getId());
                        userSimple.setFirstName(p.getUser().getFirstName());
                        userSimple.setLastName(p.getUser().getLastName());
                        userSimple.setProfilePicture(p.getUser().getProfilePicture());
                        userSimple.setRating(p.getUser().getRating());
                        userSimple.setPosition(p.getUser().getPosition());
                        userSimple.setSkillLevel(p.getUser().getSkillLevel());
                        userSimple.setCity(p.getUser().getCity());
                        dto.setUser(userSimple);

                        dto.setTeamId(p.getTeam().getId());
                        dto.setTeam(teamMapper.toDTO(p.getTeam()));
                        dto.setStatus(p.getStatus());
                        dto.setIsPaid(p.getIsPaid());
                        dto.setPaymentDate(p.getPaymentDate());
                        dto.setPaymentReference(p.getPaymentReference());
                        dto.setAmountPaid(p.getAmountPaid());
                        dto.setCreatedAt(p.getCreatedAt());
                        dto.setUpdatedAt(p.getUpdatedAt());
                        return dto;
                    })
                    .collect(Collectors.toList());

            matchDTO.setParticipations(participationDTOs);
        }

        return matchDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getAllMatches() {
        log.info("Fetching all matches");
        List<Match> matches = matchRepository.findAll();
        return matchMapper.toDTOList(matches);
    }

    @Override
    public MatchDTO updateMatch(UUID id, MatchDTO matchDTO) {
        log.info("Updating match with ID: {}", id);
        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + id));


        if (existingMatch.getStatus() == StatusMatch.COMPLETED) {
            throw new BusinessException("Cannot update a completed match");
        }

        if (!existingMatch.getTime().equals(matchDTO.getTime()) ||
                !existingMatch.getTeam1().getId().equals(matchDTO.getTeam1Id()) ||
                !existingMatch.getTeam2().getId().equals(matchDTO.getTeam2Id()) ||
                !existingMatch.getTerrain().getId().equals(matchDTO.getTerrainId())) {

            if (!existingMatch.getTerrain().getId().equals(matchDTO.getTerrainId())) {
                Terrain newTerrain = validateTerrain(matchDTO.getTerrainId());
                validateTerrainAvailability(newTerrain.getId(), matchDTO.getTime(), id);
                existingMatch.setTerrain(newTerrain);
            }

            if (!existingMatch.getTeam1().getId().equals(matchDTO.getTeam1Id())) {
                Team newTeam1 = validateTeam(matchDTO.getTeam1Id());
                validateTeamsAreDifferent(newTeam1, existingMatch.getTeam2());
                validateTeamAvailability(newTeam1.getId(), matchDTO.getTime(), id);
                existingMatch.setTeam1(newTeam1);
            }

            if (!existingMatch.getTeam2().getId().equals(matchDTO.getTeam2Id())) {
                Team newTeam2 = validateTeam(matchDTO.getTeam2Id());
                validateTeamsAreDifferent(existingMatch.getTeam1(), newTeam2);
                validateTeamAvailability(newTeam2.getId(), matchDTO.getTime(), id);
                existingMatch.setTeam2(newTeam2);
            }

            if (!existingMatch.getTime().equals(matchDTO.getTime())) {
                validateTeamAvailability(existingMatch.getTeam1().getId(), matchDTO.getTime(), id);
                validateTeamAvailability(existingMatch.getTeam2().getId(), matchDTO.getTime(), id);
                validateTerrainAvailability(existingMatch.getTerrain().getId(), matchDTO.getTime(), id);
                validateNoDuplicateMatch(existingMatch.getTeam1().getId(),
                        existingMatch.getTeam2().getId(),
                        matchDTO.getTime(), id);
            }
        }

        matchMapper.updateEntityFromDTO(matchDTO, existingMatch);
        Match updatedMatch = matchRepository.save(existingMatch);
        log.info("Match updated successfully with ID: {}", id);

        return matchMapper.toDTO(updatedMatch);
    }

    @Override
    public void deleteMatch(UUID id) {
        log.info("Deleting match with ID: {}", id);
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + id));

        if (match.getStatus() == StatusMatch.IN_PROGRESS ||
                match.getStatus() == StatusMatch.COMPLETED) {
            throw new BusinessException("Cannot delete a match that is in progress or completed");
        }

        matchRepository.delete(match);
        log.info("Match deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getMatchesByStatus(StatusMatch status) {
        log.info("Fetching matches with status: {}", status);
        List<Match> matches = matchRepository.findByStatus(status);
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getMatchesByType(MatchType matchType) {
        log.info("Fetching matches with type: {}", matchType);
        List<Match> matches = matchRepository.findByMatchType(matchType);
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getMatchesByLevel(RequiredLevel requiredLevel) {
        log.info("Fetching matches with level: {}", requiredLevel);
        List<Match> matches = matchRepository.findByRequiredLevel(requiredLevel);
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getMatchesByVisibility(Visibility visibility) {
        log.info("Fetching matches with visibility: {}", visibility);
        List<Match> matches = matchRepository.findByVisibility(visibility);
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getMatchesByTerrain(UUID terrainId) {
        log.info("Fetching matches for terrain: {}", terrainId);
        if (!terrainRepository.existsById(terrainId)) {
            throw new ResourceNotFoundException("Terrain not found with ID: " + terrainId);
        }
        List<Match> matches = matchRepository.findByTerrainId(terrainId);
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getMatchesByTeam(UUID teamId) {
        log.info("Fetching matches for team: {}", teamId);
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with ID: " + teamId);
        }
        List<Match> matches = matchRepository.findAllByTeamId(teamId);
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getUpcomingMatches() {
        log.info("Fetching upcoming matches");
        List<Match> matches = matchRepository.findUpcomingMatches(LocalDateTime.now());
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getPastMatches() {
        log.info("Fetching past matches");
        List<Match> matches = matchRepository.findPastMatches(LocalDateTime.now());
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getTodayMatches() {
        log.info("Fetching today's matches");
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        List<Match> matches = matchRepository.findByTimeBetween(startOfDay, endOfDay);
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getMatchesBetweenDates(LocalDateTime start, LocalDateTime end) {
        log.info("Fetching matches between {} and {}", start, end);
        if (start.isAfter(end)) {
            throw new BusinessException("Start date must be before end date");
        }
        List<Match> matches = matchRepository.findByTimeBetween(start, end);
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getMatchesByTerrainAndDateRange(UUID terrainId, LocalDateTime start, LocalDateTime end) {
        log.info("Fetching matches for terrain {} between {} and {}", terrainId, start, end);
        if (!terrainRepository.existsById(terrainId)) {
            throw new ResourceNotFoundException("Terrain not found with ID: " + terrainId);
        }
        if (start.isAfter(end)) {
            throw new BusinessException("Start date must be before end date");
        }
        List<Match> matches = matchRepository.findByTerrainAndDateRange(terrainId, start, end);
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getAvailableMatches() {
        log.info("Fetching available matches");
        List<Match> matches = matchRepository.findAvailableMatches(LocalDateTime.now());
        return matchMapper.toDTOList(matches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getFullMatches() {
        log.info("Fetching full matches");
        List<Match> allMatches = matchRepository.findAll();
        List<Match> fullMatches = allMatches.stream()
                .filter(Match::isFull)
                .collect(Collectors.toList());
        return matchMapper.toDTOList(fullMatches);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> searchMatches(String keyword) {
        log.info("Searching matches with keyword: {}", keyword);
        List<Match> matches = matchRepository.searchByKeyword(keyword);
        return matchMapper.toDTOList(matches);
    }

    @Override
    public MatchDTO joinMatch(UUID matchId) {
        log.info("Player joining match: {}", matchId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        if (!match.canJoin()) {
            throw new BusinessException("Cannot join this match. It may be full or not scheduled.");
        }

        match.setCurrentPlayers(match.getCurrentPlayers() + 1);
        Match updatedMatch = matchRepository.save(match);
        log.info("Player joined match successfully. Current players: {}/{}",
                updatedMatch.getCurrentPlayers(), updatedMatch.getPlayersNeeded());

        return matchMapper.toDTO(updatedMatch);
    }

    @Override
    public MatchDTO leaveMatch(UUID matchId) {
        log.info("Player leaving match: {}", matchId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        if (match.getCurrentPlayers() <= 0) {
            throw new BusinessException("No players to remove from this match");
        }

        if (match.getStatus() == StatusMatch.COMPLETED || match.getStatus() == StatusMatch.IN_PROGRESS) {
            throw new BusinessException("Cannot leave a match that is in progress or completed");
        }

        match.setCurrentPlayers(match.getCurrentPlayers() - 1);
        Match updatedMatch = matchRepository.save(match);
        log.info("Player left match successfully. Current players: {}/{}",
                updatedMatch.getCurrentPlayers(), updatedMatch.getPlayersNeeded());

        return matchMapper.toDTO(updatedMatch);
    }

    @Override
    public MatchDTO cancelMatch(UUID matchId) {
        log.info("Cancelling match: {}", matchId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        if (match.getStatus() == StatusMatch.COMPLETED) {
            throw new BusinessException("Cannot cancel a completed match");
        }

        match.setStatus(StatusMatch.CANCELLED);
        Match updatedMatch = matchRepository.save(match);
        log.info("Match cancelled successfully");

        return matchMapper.toDTO(updatedMatch);
    }

    @Override
    public MatchDTO startMatch(UUID matchId) {
        log.info("Starting match: {}", matchId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        if (match.getStatus() != StatusMatch.SCHEDULED) {
            throw new BusinessException("Only scheduled matches can be started");
        }

        if (match.getCurrentPlayers() < match.getPlayersNeeded()) {
            throw new BusinessException("Cannot start match: not enough players");
        }

        match.setStatus(StatusMatch.IN_PROGRESS);
        Match updatedMatch = matchRepository.save(match);
        log.info("Match started successfully");

        return matchMapper.toDTO(updatedMatch);
    }

    @Override
    public MatchDTO completeMatch(UUID matchId, UUID winnerTeamId, Integer score1, Integer score2) {
        log.info("Completing match: {}", matchId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        if (match.getStatus() != StatusMatch.IN_PROGRESS) {
            throw new BusinessException("Only matches in progress can be completed");
        }

        if (!match.isTeamParticipating(winnerTeamId)) {
            throw new BusinessException("Winner team must be one of the participating teams");
        }

        Team winnerTeam = teamRepository.findById(winnerTeamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + winnerTeamId));

        match.setWinnerTeam(winnerTeam);
        match.setScoreTeam1(score1);
        match.setScoreTeam2(score2);
        match.setStatus(StatusMatch.COMPLETED);

        Match updatedMatch = matchRepository.save(match);
        log.info("Match completed successfully");

        return matchMapper.toDTO(updatedMatch);
    }

    @Override
    public MatchDTO postponeMatch(UUID matchId, LocalDateTime newTime) {
        log.info("Postponing match: {} to {}", matchId, newTime);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        if (match.getStatus() == StatusMatch.COMPLETED) {
            throw new BusinessException("Cannot postpone a completed match");
        }

        validateTeamAvailability(match.getTeam1().getId(), newTime, matchId);
        validateTeamAvailability(match.getTeam2().getId(), newTime, matchId);
        validateTerrainAvailability(match.getTerrain().getId(), newTime, matchId);
        validateNoDuplicateMatch(match.getTeam1().getId(), match.getTeam2().getId(), newTime, matchId);

        match.setTime(newTime);
        match.setStatus(StatusMatch.POSTPONED);

        Match updatedMatch = matchRepository.save(match);
        log.info("Match postponed successfully");

        return matchMapper.toDTO(updatedMatch);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMatchesByStatus(StatusMatch status) {
        return matchRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMatchesByTerrain(UUID terrainId) {
        if (!terrainRepository.existsById(terrainId)) {
            throw new ResourceNotFoundException("Terrain not found with ID: " + terrainId);
        }
        return matchRepository.countByTerrainId(terrainId);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchStatistics getMatchStatistics() {
        log.info("Calculating match statistics");

        long total = matchRepository.count();
        long scheduled = matchRepository.countByStatus(StatusMatch.SCHEDULED);
        long inProgress = matchRepository.countByStatus(StatusMatch.IN_PROGRESS);
        long completed = matchRepository.countByStatus(StatusMatch.COMPLETED);
        long cancelled = matchRepository.countByStatus(StatusMatch.CANCELLED);
        long postponed = matchRepository.countByStatus(StatusMatch.POSTPONED);

        List<Match> upcoming = matchRepository.findUpcomingMatches(LocalDateTime.now());
        double avgPlayers = upcoming.stream()
                .mapToInt(Match::getCurrentPlayers)
                .average()
                .orElse(0.0);

        Match mostAnticipated = upcoming.stream()
                .max((m1, m2) -> Integer.compare(m1.getCurrentPlayers(), m2.getCurrentPlayers()))
                .orElse(null);

        List<Match> completedMatches = matchRepository.findByStatus(StatusMatch.COMPLETED);
        Match highestScoring = completedMatches.stream()
                .max((m1, m2) -> Integer.compare(
                        (m1.getScoreTeam1() + m1.getScoreTeam2()),
                        (m2.getScoreTeam1() + m2.getScoreTeam2())))
                .orElse(null);

        return MatchStatistics.builder()
                .totalMatches(total)
                .scheduled(scheduled)
                .inProgress(inProgress)
                .completed(completed)
                .cancelled(cancelled)
                .postponed(postponed)
                .averagePlayersPerMatch(avgPlayers)
                .mostAnticipated(mostAnticipated != null ? matchMapper.toDTO(mostAnticipated) : null)
                .highestScoring(highestScoring != null ? matchMapper.toDTO(highestScoring) : null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> getHeadToHead(UUID team1Id, UUID team2Id) {
        log.info("Fetching head-to-head matches between {} and {}", team1Id, team2Id);

        if (!teamRepository.existsById(team1Id) || !teamRepository.existsById(team2Id)) {
            throw new ResourceNotFoundException("One or both teams not found");
        }

        List<Match> allMatches = matchRepository.findAll();
        List<Match> headToHead = allMatches.stream()
                .filter(m -> (m.getTeam1().getId().equals(team1Id) && m.getTeam2().getId().equals(team2Id)) ||
                        (m.getTeam1().getId().equals(team2Id) && m.getTeam2().getId().equals(team1Id)))
                .sorted((m1, m2) -> m2.getTime().compareTo(m1.getTime()))
                .collect(Collectors.toList());

        return matchMapper.toDTOList(headToHead);
    }


    private Terrain validateTerrain(UUID terrainId) {
        Terrain terrain = terrainRepository.findById(terrainId)
                .orElseThrow(() -> new ResourceNotFoundException("Terrain not found with ID: " + terrainId));

        if (!terrain.getIsActive()) {
            throw new BusinessException("Terrain is not active: " + terrain.getName());
        }
        return terrain;
    }

    private Team validateTeam(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));

        if (!team.getIsActive()) {
            throw new BusinessException("Team is not active: " + team.getName());
        }
        return team;
    }

    private void validateTeamsAreDifferent(Team team1, Team team2) {
        if (team1.getId().equals(team2.getId())) {
            throw new BusinessException("A match cannot be between the same team");
        }
    }

    private void validateTeamAvailability(UUID teamId, LocalDateTime time, UUID excludeMatchId) {
        boolean isBusy;
        if (excludeMatchId != null) {
            isBusy = matchRepository.existsDuplicateMatch(teamId, teamId, time, excludeMatchId);
        } else {
            isBusy = teamRepository.isTeamBusy(teamId, time);
        }

        if (isBusy) {
            throw new BusinessException("Team is already scheduled for a match at this time");
        }
    }

    private void validateTerrainAvailability(UUID terrainId, LocalDateTime time, UUID excludeMatchId) {
        boolean isBusy;
        if (excludeMatchId != null) {
            isBusy = matchRepository.findByTerrainAndDateRange(terrainId, time, time.plusMinutes(1))
                    .stream()
                    .anyMatch(m -> !m.getId().equals(excludeMatchId));
        } else {
            isBusy = matchRepository.isTerrainBusy(terrainId, time);
        }

        if (isBusy) {
            throw new BusinessException("Terrain is already booked at this time");
        }
    }

    private void validateNoDuplicateMatch(UUID team1Id, UUID team2Id, LocalDateTime time, UUID excludeMatchId) {
        boolean exists;
        if (excludeMatchId != null) {
            exists = matchRepository.existsDuplicateMatch(team1Id, team2Id, time, excludeMatchId);
        } else {
            Team team1 = teamRepository.getReferenceById(team1Id);
            Team team2 = teamRepository.getReferenceById(team2Id);
            exists = matchRepository.existsByTeam1AndTeam2AndTime(team1, team2, time);
        }

        if (exists) {
            throw new BusinessException("A match between these teams already exists at this time");
        }
    }
    @Override
    public boolean isTerrainAvailable(UUID terrainId, LocalDateTime time, UUID excludeMatchId) {
        try {
            validateTerrainAvailability(terrainId, time, excludeMatchId);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
}