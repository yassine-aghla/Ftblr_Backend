package org.example.ftblr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.Services.MatchParticipationService;
import org.example.ftblr.dtos.MatchParticipationDTO;
import org.example.ftblr.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches/{matchId}/participations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class MatchParticipationController {

    private final MatchParticipationService participationService;

    @PostMapping("/join")
    public ResponseEntity<MatchParticipationDTO> joinMatch(
            @PathVariable UUID matchId,
            @RequestBody Map<String, UUID> request) {
        UUID teamId = request.get("teamId");
        UUID userId = getCurrentUserId();
        log.info("REST request to join match {} with team {}", matchId, teamId);
        MatchParticipationDTO participation = participationService.joinMatch(matchId, teamId, userId);
        return ResponseEntity.ok(participation);
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leaveMatch(@PathVariable UUID matchId) {
        UUID userId = getCurrentUserId();
        log.info("REST request to leave match {}", matchId);
        participationService.leaveMatch(matchId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MatchParticipationDTO>> getMatchParticipations(@PathVariable UUID matchId) {
        log.info("REST request to get participations for match {}", matchId);
        List<MatchParticipationDTO> participations = participationService.getMatchParticipations(matchId);
        return ResponseEntity.ok(participations);
    }

    @GetMapping("/my-participation")
    public ResponseEntity<MatchParticipationDTO> getMyParticipation(@PathVariable UUID matchId) {
        UUID userId = getCurrentUserId();
        log.info("REST request to get participation for user {} in match {}", userId, matchId);
        MatchParticipationDTO participation = participationService.getParticipation(matchId, userId);
        return ResponseEntity.ok(participation);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkUserInMatch(@PathVariable UUID matchId) {
        UUID userId = getCurrentUserId();
        boolean isInMatch = participationService.isUserInMatch(matchId, userId);
        return ResponseEntity.ok(Map.of("isInMatch", isInMatch));
    }

    @GetMapping("/teams/{teamId}/count")
    public ResponseEntity<Map<String, Long>> getTeamPlayersCount(
            @PathVariable UUID matchId,
            @PathVariable UUID teamId) {
        long count = participationService.getTeamPlayersCount(matchId, teamId);
        return ResponseEntity.ok(Map.of("count", count));
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