package org.example.ftblr.Repository;

import org.example.ftblr.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    Optional<Team> findByName(String name);

    boolean existsByName(String name);

    List<Team> findByIsActiveTrue();

    List<Team> findByCity(String city);

    @Query("SELECT t FROM Team t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Team> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.team1.id = :teamId OR m.team2.id = :teamId")
    long countMatchesByTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Match m " +
            "WHERE (m.team1.id = :teamId OR m.team2.id = :teamId) " +
            "AND m.time = :time AND m.status != 'CANCELLED'")
    boolean isTeamBusy(@Param("teamId") UUID teamId, @Param("time") LocalDateTime time);
}