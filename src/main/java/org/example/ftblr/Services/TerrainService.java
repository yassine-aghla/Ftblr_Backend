package org.example.ftblr.Services;

import org.example.ftblr.dtos.TerrainDTO;
import org.example.ftblr.Entity.AccesType;

import java.util.List;
import java.util.UUID;

public interface TerrainService {

    TerrainDTO createTerrain(TerrainDTO terrainDTO);
    TerrainDTO getTerrainById(UUID id);
    List<TerrainDTO> getAllTerrains();
    List<TerrainDTO> getAllActiveTerrains();
    TerrainDTO updateTerrain(UUID id, TerrainDTO terrainDTO);
    void deleteTerrain(UUID id);
    void hardDeleteTerrain(UUID id);

    List<TerrainDTO> getTerrainsByAccesType(AccesType accesType);
    List<TerrainDTO> getTerrainsWithLighting();
    List<TerrainDTO> getTerrainsWithParking();
    List<TerrainDTO> getTerrainsWithChangingRooms();


    List<TerrainDTO> getTerrainsByMinimumRating(Double minRating);
    List<TerrainDTO> getTerrainsByPlayerRange(Integer minPlayers, Integer maxPlayers);
    List<TerrainDTO> searchTerrains(String keyword);
}