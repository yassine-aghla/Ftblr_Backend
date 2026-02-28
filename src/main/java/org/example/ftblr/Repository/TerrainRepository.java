package org.example.ftblr.Repository;

import org.example.ftblr.Entity.AccesType;
import org.example.ftblr.Entity.Terrain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TerrainRepository extends JpaRepository<Terrain, UUID> {

    List<Terrain> findByIsActiveTrue();

    List<Terrain> findByAccesType(AccesType accesType);

    List<Terrain> findByHasLightingTrue();

    List<Terrain> findByHasParkingTrue();

    List<Terrain> findByHasChangingRoomsTrue();

    @Query("SELECT t FROM Terrain t WHERE t.rating >= :minRating AND t.isActive = true")
    List<Terrain> findByMinimumRating(@Param("minRating") Double minRating);

    @Query("SELECT t FROM Terrain t WHERE t.recommendedPlayers >= :minPlayers AND t.recommendedPlayers <= :maxPlayers")
    List<Terrain> findByPlayerRange(@Param("minPlayers") Integer minPlayers, @Param("maxPlayers") Integer maxPlayers);

    Optional<Terrain> findByIdAndIsActiveTrue(UUID id);

    @Query("SELECT t FROM Terrain t WHERE LOWER(t.name)  LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Terrain> searchByKeyword(@Param("keyword") String keyword);

}