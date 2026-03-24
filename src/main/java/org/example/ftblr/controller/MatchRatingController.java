package org.example.ftblr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.Services.MatchRatingService;
import org.example.ftblr.dtos.AttendanceDTO;
import org.example.ftblr.dtos.MatchResultDTO;
import org.example.ftblr.dtos.PlayerRatingDTO;
import org.example.ftblr.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches/{matchId}/ratings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MatchRatingController {

    private final MatchRatingService ratingService;

    @PostMapping("/players")
    public ResponseEntity<PlayerRatingDTO> ratePlayer(
            @PathVariable UUID matchId,
            @Valid @RequestBody PlayerRatingDTO ratingDTO) {
        UUID raterId = getCurrentUserId();
        log.info("REST request to rate player in match: {}", matchId);
        ratingDTO.setMatchId(matchId);
        PlayerRatingDTO result = ratingService.ratePlayer(raterId, ratingDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/players/{playerId}/has-rated")
    public ResponseEntity<Map<String, Boolean>> hasRatedPlayer(
            @PathVariable UUID matchId,
            @PathVariable UUID playerId) {
        UUID raterId = getCurrentUserId();
        boolean hasRated = ratingService.hasRatedPlayer(raterId, playerId, matchId);
        return ResponseEntity.ok(Map.of("hasRated", hasRated));
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerRatingDTO>> getMatchRatings(@PathVariable UUID matchId) {
        log.info("REST request to get ratings for match: {}", matchId);
        List<PlayerRatingDTO> ratings = ratingService.getMatchRatings(matchId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<List<PlayerRatingDTO>> getPlayerRatings(@PathVariable UUID playerId) {
        log.info("REST request to get ratings for player: {}", playerId);
        List<PlayerRatingDTO> ratings = ratingService.getPlayerRatings(playerId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/players/{playerId}/average")
    public ResponseEntity<Map<String, Double>> getPlayerAverageRating(@PathVariable UUID playerId) {
        log.info("REST request to get average rating for player: {}", playerId);
        Double average = ratingService.getPlayerAverageRating(playerId);
        return ResponseEntity.ok(Map.of("average", average != null ? average : 0.0));
    }

    @PostMapping("/result")
    public ResponseEntity<Void> submitMatchResult(
            @PathVariable UUID matchId,
            @Valid @RequestBody MatchResultDTO resultDTO) {
        UUID organizerId = getCurrentUserId();
        log.info("REST request to submit result for match: {}", matchId);
        resultDTO.setMatchId(matchId);
        ratingService.submitMatchResult(organizerId, resultDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/result/submitted")
    public ResponseEntity<Map<String, Boolean>> isMatchResultSubmitted(@PathVariable UUID matchId) {
        boolean submitted = ratingService.isMatchResultSubmitted(matchId);
        return ResponseEntity.ok(Map.of("submitted", submitted));
    }

    @PostMapping("/attendance")
    public ResponseEntity<Void> markAttendance(
            @PathVariable UUID matchId,
            @RequestBody List<AttendanceDTO> attendances) {
        log.info("REST request to mark attendance for match: {}", matchId);
        ratingService.markAttendance(matchId, attendances);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<AttendanceDTO>> getMatchAttendance(@PathVariable UUID matchId) {
        log.info("REST request to get attendance for match: {}", matchId);
        List<AttendanceDTO> attendances = ratingService.getMatchAttendance(matchId);
        return ResponseEntity.ok(attendances);
    }

    @GetMapping("/attendance/absent")
    public ResponseEntity<List<UUID>> getAbsentPlayers(@PathVariable UUID matchId) {
        log.info("REST request to get absent players for match: {}", matchId);
        List<UUID> absentPlayers = ratingService.getAbsentPlayers(matchId);
        return ResponseEntity.ok(absentPlayers);
    }

    @PostMapping("/players/{playerId}/ban")
    public ResponseEntity<Void> banPlayer(
            @PathVariable UUID playerId,
            @RequestParam String reason) {
        UUID adminId = getCurrentUserId();
        log.info("REST request to ban player: {} with reason: {}", playerId, reason);
        ratingService.banPlayer(adminId, playerId, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/players/{playerId}/warn")
    public ResponseEntity<Void> warnPlayer(
            @PathVariable UUID playerId,
            @RequestParam String reason) {
        UUID adminId = getCurrentUserId();
        log.info("REST request to warn player: {} with reason: {}", playerId, reason);
        ratingService.warnPlayer(adminId, playerId, reason);
        return ResponseEntity.ok().build();
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getId();
        }
        throw new RuntimeException("User not authenticated");
    }
}