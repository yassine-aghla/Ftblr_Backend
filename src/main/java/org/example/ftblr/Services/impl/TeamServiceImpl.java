package org.example.ftblr.Services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.dtos.TeamDTO;
import org.example.ftblr.Entity.Match;
import org.example.ftblr.Entity.Team;
import org.example.ftblr.exception.BusinessException;
import org.example.ftblr.exception.ResourceNotFoundException;
import org.example.ftblr.mapper.TeamMapper;
import org.example.ftblr.Repository.MatchRepository;
import org.example.ftblr.Repository.TeamRepository;
import org.example.ftblr.Services.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final TeamMapper teamMapper;

    @Override
    public TeamDTO createTeam(TeamDTO teamDTO) {
        log.info("Creating new team: {}", teamDTO.getName());

        if (teamRepository.existsByName(teamDTO.getName())) {
            throw new BusinessException("Team with name '" + teamDTO.getName() + "' already exists");
        }

        Team team = teamMapper.toEntity(teamDTO);
        Team savedTeam = teamRepository.save(team);
        log.info("Team created successfully with ID: {}", savedTeam.getId());
        return teamMapper.toDTO(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDTO getTeamById(UUID id) {
        log.info("Fetching team with ID: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        return teamMapper.toDTO(team);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDTO getTeamByName(String name) {
        log.info("Fetching team with name: {}", name);
        Team team = teamRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with name: " + name));
        return teamMapper.toDTO(team);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDTO> getAllTeams() {
        log.info("Fetching all teams");
        List<Team> teams = teamRepository.findAll();
        return teamMapper.toDTOList(teams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDTO> getAllActiveTeams() {
        log.info("Fetching all active teams");
        List<Team> teams = teamRepository.findByIsActiveTrue();
        return teamMapper.toDTOList(teams);
    }

    @Override
    public TeamDTO updateTeam(UUID id, TeamDTO teamDTO) {
        log.info("Updating team with ID: {}", id);
        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));

        if (!existingTeam.getName().equals(teamDTO.getName()) &&
                teamRepository.existsByName(teamDTO.getName())) {
            throw new BusinessException("Team with name '" + teamDTO.getName() + "' already exists");
        }

        teamMapper.updateEntityFromDTO(teamDTO, existingTeam);
        Team updatedTeam = teamRepository.save(existingTeam);
        log.info("Team updated successfully with ID: {}", id);
        return teamMapper.toDTO(updatedTeam);
    }

    @Override
    public void deleteTeam(UUID id) {
        log.info("Deleting team with ID: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));

        long matchesCount = matchRepository.countMatchesByTeamId(id);
        if (matchesCount > 0) {
            throw new BusinessException("Cannot delete team with " + matchesCount + " existing matches");
        }

        teamRepository.delete(team);
        log.info("Team deleted successfully with ID: {}", id);
    }

    @Override
    public void activateTeam(UUID id) {
        log.info("Activating team with ID: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        team.setIsActive(true);
        teamRepository.save(team);
        log.info("Team activated successfully");
    }

    @Override
    public void deactivateTeam(UUID id) {
        log.info("Deactivating team with ID: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        team.setIsActive(false);
        teamRepository.save(team);
        log.info("Team deactivated successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDTO> getTeamsByCity(String city) {
        log.info("Fetching teams from city: {}", city);
        List<Team> teams = teamRepository.findByCity(city);
        return teamMapper.toDTOList(teams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDTO> searchTeams(String keyword) {
        log.info("Searching teams with keyword: {}", keyword);
        List<Team> teams = teamRepository.searchByKeyword(keyword);
        return teamMapper.toDTOList(teams);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamStatistics getTeamStatistics(UUID teamId) {
        log.info("Calculating statistics for team ID: {}", teamId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));

        long totalMatches = matchRepository.countMatchesByTeamId(teamId);
        long wins = matchRepository.countWinsByTeamId(teamId);
        long losses = totalMatches - wins;
        long homeMatches = matchRepository.countHomeMatchesByTeamId(teamId);
        long awayMatches = matchRepository.countAwayMatchesByTeamId(teamId);

        return TeamStatistics.builder()
                .teamId(teamId)
                .teamName(team.getName())
                .totalMatches(totalMatches)
                .wins(wins)
                .losses(losses)
                .winRate(totalMatches > 0 ? (double) wins / totalMatches * 100 : 0.0)
                .homeMatches(homeMatches)
                .awayMatches(awayMatches)
                .build();
    }
}