package org.example.ftblr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.dtos.TeamDTO;
import org.example.ftblr.Services.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamService teamService;


    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        log.info("REST request to create team: {}", teamDTO.getName());
        TeamDTO createdTeam = teamService.createTeam(teamDTO);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable UUID id) {
        log.info("REST request to get team by ID: {}", id);
        TeamDTO team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TeamDTO> getTeamByName(@PathVariable String name) {
        log.info("REST request to get team by name: {}", name);
        TeamDTO team = teamService.getTeamByName(name);
        return ResponseEntity.ok(team);
    }


    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams(
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        log.info("REST request to get all teams (activeOnly: {})", activeOnly);
        List<TeamDTO> teams = activeOnly
                ? teamService.getAllActiveTeams()
                : teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable UUID id,
            @Valid @RequestBody TeamDTO teamDTO) {
        log.info("REST request to update team: {}", id);
        TeamDTO updatedTeam = teamService.updateTeam(id, teamDTO);
        return ResponseEntity.ok(updatedTeam);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        log.info("REST request to delete team: {}", id);
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateTeam(@PathVariable UUID id) {
        log.info("REST request to activate team: {}", id);
        teamService.activateTeam(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateTeam(@PathVariable UUID id) {
        log.info("REST request to deactivate team: {}", id);
        teamService.deactivateTeam(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/city/{city}")
    public ResponseEntity<List<TeamDTO>> getTeamsByCity(@PathVariable String city) {
        log.info("REST request to get teams from city: {}", city);
        List<TeamDTO> teams = teamService.getTeamsByCity(city);
        return ResponseEntity.ok(teams);
    }


    @GetMapping("/search")
    public ResponseEntity<List<TeamDTO>> searchTeams(@RequestParam String q) {
        log.info("REST request to search teams with keyword: {}", q);
        List<TeamDTO> teams = teamService.searchTeams(q);
        return ResponseEntity.ok(teams);
    }


    @GetMapping("/{id}/statistics")
    public ResponseEntity<TeamService.TeamStatistics> getTeamStatistics(@PathVariable UUID id) {
        log.info("REST request to get statistics for team: {}", id);
        TeamService.TeamStatistics stats = teamService.getTeamStatistics(id);
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/active")
    public ResponseEntity<List<TeamDTO>> getActiveTeams() {
        log.info("REST request to get all active teams");
        List<TeamDTO> teams = teamService.getAllActiveTeams();
        return ResponseEntity.ok(teams);
    }


    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkTeamNameAvailability(@RequestParam String name) {
        log.info("REST request to check team name availability: {}", name);
        try {
            teamService.getTeamByName(name);
            return ResponseEntity.ok(false); // Nom déjà pris
        } catch (Exception e) {
            return ResponseEntity.ok(true); // Nom disponible
        }
    }
}