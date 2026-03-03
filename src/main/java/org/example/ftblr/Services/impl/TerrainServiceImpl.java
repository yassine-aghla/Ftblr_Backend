package org.example.ftblr.Services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.dtos.TerrainDTO;
import org.example.ftblr.Entity.AccesType;
import org.example.ftblr.Entity.Terrain;
import org.example.ftblr.exception.ResourceNotFoundException;
import org.example.ftblr.mapper.TerrainMapper;
import org.example.ftblr.Repository.TerrainRepository;
import org.example.ftblr.Services.TerrainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TerrainServiceImpl implements TerrainService {

    private final TerrainRepository terrainRepository;
    private final TerrainMapper terrainMapper;

    @Override
    public TerrainDTO createTerrain(TerrainDTO terrainDTO) {
        log.info("Creating new terrain: {}", terrainDTO.getName());
        Terrain terrain = terrainMapper.toEntity(terrainDTO);
        Terrain savedTerrain = terrainRepository.save(terrain);
        log.info("Terrain created successfully with ID: {}", savedTerrain.getId());
        return terrainMapper.toDTO(savedTerrain);
    }

    @Override
    @Transactional(readOnly = true)
    public TerrainDTO getTerrainById(UUID id) {
        log.info("Fetching terrain with ID: {}", id);
        Terrain terrain = terrainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Terrain not found with ID: " + id));
        return terrainMapper.toDTO(terrain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerrainDTO> getAllTerrains() {
        log.info("Fetching all terrains");
        List<Terrain> terrains = terrainRepository.findAll();
        return terrainMapper.toDTOList(terrains);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerrainDTO> getAllActiveTerrains() {
        log.info("Fetching all active terrains");
        List<Terrain> terrains = terrainRepository.findByIsActiveTrue();
        return terrainMapper.toDTOList(terrains);
    }

    @Override
    public TerrainDTO updateTerrain(UUID id, TerrainDTO terrainDTO) {
        log.info("Updating terrain with ID: {}", id);
        Terrain existingTerrain = terrainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Terrain not found with ID: " + id));

        terrainMapper.updateEntityFromDTO(terrainDTO, existingTerrain);
        Terrain updatedTerrain = terrainRepository.save(existingTerrain);
        log.info("Terrain updated successfully with ID: {}", id);
        return terrainMapper.toDTO(updatedTerrain);
    }

    @Override
    public void deleteTerrain(UUID id) {
        log.info("Soft deleting terrain with ID: {}", id);
        Terrain terrain = terrainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Terrain not found with ID: " + id));
        terrain.setIsActive(false);
        terrainRepository.save(terrain);
        log.info("Terrain soft deleted successfully with ID: {}", id);
    }

    @Override
    public void hardDeleteTerrain(UUID id) {
        log.info("Hard deleting terrain with ID: {}", id);
        if (!terrainRepository.existsById(id)) {
            throw new ResourceNotFoundException("Terrain not found with ID: " + id);
        }
        terrainRepository.deleteById(id);
        log.info("Terrain hard deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerrainDTO> getTerrainsByAccesType(AccesType accesType) {
        log.info("Fetching terrains with access type: {}", accesType);
        List<Terrain> terrains = terrainRepository.findByAccesType(accesType);
        return terrainMapper.toDTOList(terrains);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerrainDTO> getTerrainsWithLighting() {
        log.info("Fetching terrains with lighting");
        List<Terrain> terrains = terrainRepository.findByHasLightingTrue();
        return terrainMapper.toDTOList(terrains);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerrainDTO> getTerrainsWithParking() {
        log.info("Fetching terrains with parking");
        List<Terrain> terrains = terrainRepository.findByHasParkingTrue();
        return terrainMapper.toDTOList(terrains);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerrainDTO> getTerrainsWithChangingRooms() {
        log.info("Fetching terrains with changing rooms");
        List<Terrain> terrains = terrainRepository.findByHasChangingRoomsTrue();
        return terrainMapper.toDTOList(terrains);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerrainDTO> getTerrainsByMinimumRating(Double minRating) {
        log.info("Fetching terrains with minimum rating: {}", minRating);
        List<Terrain> terrains = terrainRepository.findByMinimumRating(minRating);
        return terrainMapper.toDTOList(terrains);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerrainDTO> getTerrainsByPlayerRange(Integer minPlayers, Integer maxPlayers) {
        log.info("Fetching terrains for {} to {} players", minPlayers, maxPlayers);
        List<Terrain> terrains = terrainRepository.findByPlayerRange(minPlayers, maxPlayers);
        return terrainMapper.toDTOList(terrains);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerrainDTO> searchTerrains(String keyword) {
        log.info("Searching terrains with keyword: {}", keyword);
        List<Terrain> terrains = terrainRepository.searchByKeyword(keyword);
        return terrainMapper.toDTOList(terrains);
    }
}