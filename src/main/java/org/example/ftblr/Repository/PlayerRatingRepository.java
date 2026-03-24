package org.example.ftblr.Repository;

import org.example.ftblr.Entity.PlayerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRatingRepository extends JpaRepository<PlayerRating, UUID> {

    Optional<PlayerRating> findByRaterIdAndRatedPlayerIdAndMatchId(UUID raterId, UUID ratedPlayerId, UUID matchId);

    List<PlayerRating> findByMatchId(UUID matchId);

    List<PlayerRating> findByRatedPlayerId(UUID playerId);

    @Query("SELECT AVG(pr.rating) FROM PlayerRating pr WHERE pr.ratedPlayer.id = :playerId")
    Double getAverageRatingForPlayer(@Param("playerId") UUID playerId);

    boolean existsByRaterIdAndRatedPlayerIdAndMatchId(UUID raterId, UUID ratedPlayerId, UUID matchId);
}