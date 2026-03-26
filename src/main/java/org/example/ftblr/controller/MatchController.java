package org.example.ftblr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.dtos.MatchDTO;
import org.example.ftblr.Entity.*;
import org.example.ftblr.Services.MatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;




    @PostMapping
    public ResponseEntity<MatchDTO> createMatch(@Valid @RequestBody MatchDTO matchDTO) {
        log.info("REST request to create match: {}", matchDTO.getTitre());
        MatchDTO createdMatch = matchService.createMatch(matchDTO);
        return new ResponseEntity<>(createdMatch, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatchById(@PathVariable UUID id) {
        log.info("REST request to get match: {}", id);
        MatchDTO match = matchService.getMatchById(id);
        return ResponseEntity.ok(match);
    }

    @GetMapping
    public ResponseEntity<List<MatchDTO>> getAllMatches() {
        log.info("REST request to get all matches");
        List<MatchDTO> matches = matchService.getAllMatches();
        return ResponseEntity.ok(matches);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchDTO> updateMatch(
            @PathVariable UUID id,
            @Valid @RequestBody MatchDTO matchDTO) {
        log.info("REST request to update match: {}", id);
        MatchDTO updatedMatch = matchService.updateMatch(id, matchDTO);
        return ResponseEntity.ok(updatedMatch);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable UUID id) {
        log.info("REST request to delete match: {}", id);
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MatchDTO>> getMatchesByStatus(@PathVariable StatusMatch status) {
        log.info("REST request to get matches by status: {}", status);
        List<MatchDTO> matches = matchService.getMatchesByStatus(status);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/type/{matchType}")
    public ResponseEntity<List<MatchDTO>> getMatchesByType(@PathVariable MatchType matchType) {
        log.info("REST request to get matches by type: {}", matchType);
        List<MatchDTO> matches = matchService.getMatchesByType(matchType);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/level/{requiredLevel}")
    public ResponseEntity<List<MatchDTO>> getMatchesByLevel(@PathVariable RequiredLevel requiredLevel) {
        log.info("REST request to get matches by level: {}", requiredLevel);
        List<MatchDTO> matches = matchService.getMatchesByLevel(requiredLevel);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<MatchDTO>> getMatchesByVisibility(@PathVariable Visibility visibility) {
        log.info("REST request to get matches by visibility: {}", visibility);
        List<MatchDTO> matches = matchService.getMatchesByVisibility(visibility);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/terrain/{terrainId}")
    public ResponseEntity<List<MatchDTO>> getMatchesByTerrain(@PathVariable UUID terrainId) {
        log.info("REST request to get matches for terrain: {}", terrainId);
        List<MatchDTO> matches = matchService.getMatchesByTerrain(terrainId);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MatchDTO>> getMatchesByTeam(@PathVariable UUID teamId) {
        log.info("REST request to get matches for team: {}", teamId);
        List<MatchDTO> matches = matchService.getMatchesByTeam(teamId);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/filter/upcoming")
    public ResponseEntity<List<MatchDTO>> getUpcomingMatches() {
        log.info("REST request to get upcoming matches");
        List<MatchDTO> matches = matchService.getUpcomingMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/filter/past")
    public ResponseEntity<List<MatchDTO>> getPastMatches() {
        log.info("REST request to get past matches");
        List<MatchDTO> matches = matchService.getPastMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/filter/today")
    public ResponseEntity<List<MatchDTO>> getTodayMatches() {
        log.info("REST request to get today's matches");
        List<MatchDTO> matches = matchService.getTodayMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/filter/date-range")
    public ResponseEntity<List<MatchDTO>> getMatchesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("REST request to get matches between {} and {}", start, end);
        List<MatchDTO> matches = matchService.getMatchesBetweenDates(start, end);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/terrain/{terrainId}/date-range")
    public ResponseEntity<List<MatchDTO>> getMatchesByTerrainAndDateRange(
            @PathVariable UUID terrainId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("REST request to get matches for terrain {} between {} and {}", terrainId, start, end);
        List<MatchDTO> matches = matchService.getMatchesByTerrainAndDateRange(terrainId, start, end);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/filter/available")
    public ResponseEntity<List<MatchDTO>> getAvailableMatches() {
        log.info("REST request to get available matches");
        List<MatchDTO> matches = matchService.getAvailableMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/filter/full")
    public ResponseEntity<List<MatchDTO>> getFullMatches() {
        log.info("REST request to get full matches");
        List<MatchDTO> matches = matchService.getFullMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MatchDTO>> searchMatches(@RequestParam String q) {
        log.info("REST request to search matches with keyword: {}", q);
        List<MatchDTO> matches = matchService.searchMatches(q);
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<MatchDTO> joinMatch(@PathVariable UUID id) {
        log.info("REST request to join match: {}", id);
        MatchDTO match = matchService.joinMatch(id);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<MatchDTO> leaveMatch(@PathVariable UUID id) {
        log.info("REST request to leave match: {}", id);
        MatchDTO match = matchService.leaveMatch(id);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<MatchDTO> cancelMatch(@PathVariable UUID id) {
        log.info("REST request to cancel match: {}", id);
        MatchDTO match = matchService.cancelMatch(id);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<MatchDTO> startMatch(@PathVariable UUID id) {
        log.info("REST request to start match: {}", id);
        MatchDTO match = matchService.startMatch(id);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<MatchDTO> completeMatch(
            @PathVariable UUID id,
            @RequestParam UUID winnerTeamId,
            @RequestParam Integer score1,
            @RequestParam Integer score2) {
        log.info("REST request to complete match: {}", id);
        MatchDTO match = matchService.completeMatch(id, winnerTeamId, score1, score2);
        return ResponseEntity.ok(match);
    }

    @PostMapping("/{id}/postpone")
    public ResponseEntity<MatchDTO> postponeMatch(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newTime) {
        log.info("REST request to postpone match: {} to {}", id, newTime);
        MatchDTO match = matchService.postponeMatch(id, newTime);
        return ResponseEntity.ok(match);
    }

    @GetMapping("/head-to-head")
    public ResponseEntity<List<MatchDTO>> getHeadToHead(
            @RequestParam UUID team1Id,
            @RequestParam UUID team2Id) {
        log.info("REST request to get head-to-head matches between {} and {}", team1Id, team2Id);
        List<MatchDTO> matches = matchService.getHeadToHead(team1Id, team2Id);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/statistics")
    public ResponseEntity<MatchService.MatchStatistics> getMatchStatistics() {
        log.info("REST request to get match statistics");
        MatchService.MatchStatistics stats = matchService.getMatchStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/status")
    public ResponseEntity<Map<String, Long>> getMatchesCountByStatus() {
        log.info("REST request to get matches count by status");
        Map<String, Long> stats = new HashMap<>();
        stats.put("SCHEDULED", matchService.countMatchesByStatus(StatusMatch.SCHEDULED));
        stats.put("IN_PROGRESS", matchService.countMatchesByStatus(StatusMatch.IN_PROGRESS));
        stats.put("COMPLETED", matchService.countMatchesByStatus(StatusMatch.COMPLETED));
        stats.put("CANCELLED", matchService.countMatchesByStatus(StatusMatch.CANCELLED));
        stats.put("POSTPONED", matchService.countMatchesByStatus(StatusMatch.POSTPONED));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/terrain/{terrainId}")
    public ResponseEntity<Map<String, Long>> getMatchesCountByTerrain(@PathVariable UUID terrainId) {
        log.info("REST request to get matches count for terrain: {}", terrainId);
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalMatches", matchService.countMatchesByTerrain(terrainId));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}/is-full")
    public ResponseEntity<Map<String, Boolean>> isMatchFull(@PathVariable UUID id) {
        log.info("REST request to check if match is full: {}", id);
        MatchDTO match = matchService.getMatchById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFull", match.getIsFull());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/remaining-slots")
    public ResponseEntity<Map<String, Integer>> getRemainingSlots(@PathVariable UUID id) {
        log.info("REST request to get remaining slots for match: {}", id);
        MatchDTO match = matchService.getMatchById(id);
        Map<String, Integer> response = new HashMap<>();
        response.put("remainingSlots", match.getRemainingSlots());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/recent")
    public ResponseEntity<List<MatchDTO>> getRecentMatches() {
        log.info("REST request to get recent matches");
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        List<MatchDTO> matches = matchService.getMatchesBetweenDates(startDate, endDate);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/team/{teamId}/upcoming")
    public ResponseEntity<List<MatchDTO>> getUpcomingMatchesByTeam(@PathVariable UUID teamId) {
        log.info("REST request to get upcoming matches for team: {}", teamId);
        List<MatchDTO> allTeamMatches = matchService.getMatchesByTeam(teamId);
        List<MatchDTO> upcomingMatches = allTeamMatches.stream()
                .filter(m -> m.getTime().isAfter(LocalDateTime.now()))
                .filter(m -> m.getStatus() == StatusMatch.SCHEDULED)
                .toList();
        return ResponseEntity.ok(upcomingMatches);
    }

    @GetMapping("/terrain-availability")
    public ResponseEntity<Boolean> checkTerrainAvailability(
            @RequestParam UUID terrainId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("REST request to check availability for terrain {} from {} to {}", terrainId, start, end);

        boolean isAvailable = matchService.isTerrainAvailable(terrainId, start, null);
        return ResponseEntity.ok(isAvailable);
    }


    @GetMapping("/terrain-matches")
    public ResponseEntity<List<MatchDTO>> getTerrainMatches(
            @RequestParam UUID terrainId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("REST request to get matches for terrain {} on {}", terrainId, date);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<MatchDTO> matches = matchService.getMatchesByTerrainAndDateRange(terrainId, startOfDay, endOfDay);
        return ResponseEntity.ok(matches);
    }
}