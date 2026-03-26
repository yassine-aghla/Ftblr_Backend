package org.example.ftblr.Repository;

import org.example.ftblr.Entity.AttendanceStatus;
import org.example.ftblr.Entity.PlayerAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerAttendanceRepository extends JpaRepository<PlayerAttendance, UUID> {
    List<PlayerAttendance> findByMatchId(UUID matchId);

    List<PlayerAttendance> findByMatchIdAndStatus(UUID matchId, AttendanceStatus status);

    List<PlayerAttendance> findByPlayerIdAndStatus(UUID playerId, AttendanceStatus status);

    long countByPlayerIdAndStatus(UUID playerId, AttendanceStatus status);

    @Query("SELECT DISTINCT a.player.id FROM PlayerAttendance a WHERE a.status = :status")
    List<UUID> findDistinctPlayerIdsByStatus(@Param("status") AttendanceStatus status);

}