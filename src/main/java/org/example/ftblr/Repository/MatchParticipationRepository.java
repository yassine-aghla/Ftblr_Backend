package org.example.ftblr.Repository;

import org.example.ftblr.Entity.MatchParticipation;
import org.example.ftblr.Entity.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchParticipationRepository extends JpaRepository<MatchParticipation, UUID> {

    List<MatchParticipation> findByMatchId(UUID matchId);

    List<MatchParticipation> findByUserId(UUID userId);

    Optional<MatchParticipation> findByMatchIdAndUserId(UUID matchId, UUID userId);

    List<MatchParticipation> findByMatchIdAndTeamId(UUID matchId, UUID teamId);

    List<MatchParticipation> findByMatchIdAndStatus(UUID matchId, ParticipationStatus status);

    boolean existsByMatchIdAndUserId(UUID matchId, UUID userId);

    @Query("SELECT COUNT(mp) FROM MatchParticipation mp WHERE mp.match.id = :matchId AND mp.team.id = :teamId")
    long countByMatchIdAndTeamId(@Param("matchId") UUID matchId, @Param("teamId") UUID teamId);

    @Query("SELECT mp FROM MatchParticipation mp WHERE mp.match.id = :matchId ORDER BY mp.createdAt DESC")
    List<MatchParticipation> findByMatchIdOrderByCreatedAtDesc(@Param("matchId") UUID matchId);
}