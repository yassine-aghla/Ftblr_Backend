package org.example.ftblr.Repository;

import org.example.ftblr.Entity.MatchGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchGoalRepository extends JpaRepository<MatchGoal, UUID> {
    List<MatchGoal> findByMatchId(UUID matchId);
    int countByScorerId(UUID scorerId);
    @Query("SELECT COUNT(g) FROM MatchGoal g WHERE g.assistant.id = :assistId")
    int countByAssistId(@Param("assistId") UUID assistId);
}