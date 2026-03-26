package org.example.ftblr.Repository;

import org.example.ftblr.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    // Vérifications d'unicité
    boolean existsByTeam1AndTeam2AndTime(Team team1, Team team2, LocalDateTime time);

    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE " +
            "((m.team1.id = :team1Id AND m.team2.id = :team2Id) OR " +
            "(m.team1.id = :team2Id AND m.team2.id = :team1Id)) AND " +
            "m.time = :time AND m.id != :excludeId")
    boolean existsDuplicateMatch(@Param("team1Id") UUID team1Id,
                                 @Param("team2Id") UUID team2Id,
                                 @Param("time") LocalDateTime time,
                                 @Param("excludeId") UUID excludeId);

    // Filtres par statut
    List<Match> findByStatus(StatusMatch status);
    List<Match> findByMatchType(MatchType matchType);
    List<Match> findByRequiredLevel(RequiredLevel requiredLevel);
    List<Match> findByVisibility(Visibility visibility);
    List<Match> findByTerrainId(UUID terrainId);

    // Filtres temporels
    List<Match> findByTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT m FROM Match m WHERE m.time > :now ORDER BY m.time ASC")
    List<Match> findUpcomingMatches(@Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE m.time < :now ORDER BY m.time DESC")
    List<Match> findPastMatches(@Param("now") LocalDateTime now);

    // Matchs disponibles
    @Query("SELECT m FROM Match m WHERE m.currentPlayers < m.playersNeeded " +
            "AND m.status = 'SCHEDULED' AND m.time > :now")
    List<Match> findAvailableMatches(@Param("now") LocalDateTime now);

    // Recherche par équipe
    @Query("SELECT m FROM Match m WHERE m.team1.id = :teamId OR m.team2.id = :teamId")
    List<Match> findAllByTeamId(@Param("teamId") UUID teamId);

    // ✅ COMPTER LES MATCHS D'UNE ÉQUIPE
    @Query("SELECT COUNT(m) FROM Match m WHERE m.team1.id = :teamId OR m.team2.id = :teamId")
    long countMatchesByTeamId(@Param("teamId") UUID teamId);

    // ✅ COMPTER LES VICTOIRES D'UNE ÉQUIPE
    @Query("SELECT COUNT(m) FROM Match m WHERE m.winnerTeam.id = :teamId")
    long countWinsByTeamId(@Param("teamId") UUID teamId);

    // ✅ COMPTER LES MATCHS À DOMICILE
    @Query("SELECT COUNT(m) FROM Match m WHERE m.team1.id = :teamId")
    long countHomeMatchesByTeamId(@Param("teamId") UUID teamId);

    // ✅ COMPTER LES MATCHS À L'EXTÉRIEUR
    @Query("SELECT COUNT(m) FROM Match m WHERE m.team2.id = :teamId")
    long countAwayMatchesByTeamId(@Param("teamId") UUID teamId);

    // Recherche par mot-clé
    @Query("SELECT m FROM Match m WHERE LOWER(m.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Match> searchByKeyword(@Param("keyword") String keyword);

    // Compteurs
    long countByStatus(StatusMatch status);
    long countByTerrainId(UUID terrainId);

    // Matchs d'un terrain sur une période
    @Query("SELECT m FROM Match m WHERE m.terrain.id = :terrainId " +
            "AND m.time BETWEEN :startDate AND :endDate")
    List<Match> findByTerrainAndDateRange(@Param("terrainId") UUID terrainId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    // Vérifier disponibilité d'un terrain
    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE m.terrain.id = :terrainId " +
            "AND m.time = :time AND m.status != 'CANCELLED'")
    boolean isTerrainBusy(@Param("terrainId") UUID terrainId, @Param("time") LocalDateTime time);

    @Query("SELECT AVG(m.currentPlayers) FROM Match m WHERE m.status = 'SCHEDULED'")
    Double averagePlayersPerMatch();

    @Query("SELECT m FROM Match m WHERE m.status = 'SCHEDULED' ORDER BY m.currentPlayers DESC")
    List<Match> findMostAnticipatedMatch();

    @Query("SELECT m FROM Match m WHERE m.status = 'COMPLETED' ORDER BY (m.scoreTeam1 + m.scoreTeam2) DESC")
    List<Match> findHighestScoringMatch();

    @Query("SELECT m FROM Match m " +
            "LEFT JOIN FETCH m.participations p " +
            "LEFT JOIN FETCH p.user " +
            "LEFT JOIN FETCH p.team " +
            "WHERE m.id = :id")
    Optional<Match> findByIdWithParticipations(@Param("id") UUID id);

    @Query("SELECT m FROM Match m WHERE " +
            "(m.team1.id = :team1Id AND m.team2.id = :team2Id) OR " +
            "(m.team1.id = :team2Id AND m.team2.id = :team1Id) " +
            "ORDER BY m.time DESC")
    List<Match> findHeadToHeadMatches(@Param("team1Id") UUID team1Id,
                                      @Param("team2Id") UUID team2Id);
}