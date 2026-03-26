package org.example.ftblr.Repository;

import org.example.ftblr.Entity.PositionStatus;
import org.example.ftblr.Entity.Role;
import org.example.ftblr.Entity.SkillLevel;
import org.example.ftblr.Entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByIsActiveTrue();

    List<User> findByRole(Role role);

    List<User> findBySkillLevel(SkillLevel skillLevel);

    List<User> findByPosition(PositionStatus position);

    List<User> findByCity(String city);

    @Query("SELECT u FROM User u WHERE u.rating >= :minRating AND u.isActive = true")
    List<User> findByMinimumRating(@Param("minRating") Integer minRating);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE u.city = :city AND u.skillLevel = :skillLevel AND u.isActive = true")
    List<User> findByCityAndSkillLevel(@Param("city") String city, @Param("skillLevel") SkillLevel skillLevel);

    @Query("SELECT u FROM User u WHERE u.position = :position AND u.skillLevel = :skillLevel AND u.isActive = true")
    List<User> findByPositionAndSkillLevel(@Param("position") PositionStatus position, @Param("skillLevel") SkillLevel skillLevel);

    @Query(value = "SELECT * FROM users u WHERE " +
            "u.is_active = true AND " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(u.latitude)) * " +
            "cos(radians(u.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(u.latitude)))) <= :radiusKm",
            nativeQuery = true)
    List<User> findUsersNearLocation(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm
    );

    Long countByRole(Role role);

    Long countByIsActiveTrue();

    Long countBySkillLevel(SkillLevel skillLevel);

    @Query("SELECT avg(u.rating) from User u")
    Double getMoyenneRating();
    @Query("SELECT u.id as id, u.firstName as firstName, u.lastName as lastName, " +
            "u.profilePicture as profilePicture, u.position as position, " +
            "COUNT(DISTINCT g.id) as goals, " +
            "COUNT(DISTINCT a.id) as assists, " +
            "COUNT(DISTINCT m.id) as matchesPlayed, " +
            "COALESCE(AVG(pr.rating), 0) as avgRating, " +
            "CASE WHEN COUNT(att.id) = 0 THEN 100 " +
            "     ELSE (COUNT(CASE WHEN att.status = 'PRESENT' THEN 1 END) * 100.0 / COUNT(att.id)) " +
            "END as attendanceRate " +
            "FROM User u " +
            "LEFT JOIN MatchParticipation mp ON mp.user.id = u.id " +
            "LEFT JOIN Match m ON m.id = mp.match.id AND m.status = 'COMPLETED' " +
            "LEFT JOIN MatchGoal g ON g.scorer.id = u.id AND g.match.id = m.id " +
            "LEFT JOIN MatchGoal a ON a.assistant.id = u.id AND a.match.id = m.id " +
            "LEFT JOIN PlayerAttendance att ON att.player.id = u.id AND att.match.id = m.id " +
            "LEFT JOIN PlayerRating pr ON pr.ratedPlayer.id = u.id " +
            "WHERE m.time BETWEEN :startDate AND :endDate " +
            "GROUP BY u.id " +
            "HAVING COUNT(DISTINCT m.id) > 0 " +
            "AND (COUNT(CASE WHEN att.status = 'ABSENT' THEN 1 END) = 0) " +
            "ORDER BY goals DESC, assists DESC, avgRating DESC")
    List<PlayerStatsProjection> findTopPlayersForPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}