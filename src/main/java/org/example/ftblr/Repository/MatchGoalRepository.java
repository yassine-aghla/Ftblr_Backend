package org.example.ftblr.Repository;

import org.example.ftblr.Entity.MatchGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchGoalRepository extends JpaRepository<MatchGoal, UUID> {
    List<MatchGoal> findByMatchId(UUID matchId);
    long countByScorerId(UUID scorerId);
}