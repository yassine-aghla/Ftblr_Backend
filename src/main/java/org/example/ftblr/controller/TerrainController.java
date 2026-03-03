package org.example.ftblr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ftblr.dtos.TerrainDTO;
import org.example.ftblr.Entity.AccesType;
import org.example.ftblr.Services.TerrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/terrains")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TerrainController {

    private final TerrainService terrainService;

    @PostMapping
    public ResponseEntity<TerrainDTO> createTerrain(@Valid @RequestBody TerrainDTO terrainDTO) {
        log.info("REST request to create terrain: {}", terrainDTO.getName());
        TerrainDTO createdTerrain = terrainService.createTerrain(terrainDTO);
        return new ResponseEntity<>(createdTerrain, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TerrainDTO> getTerrainById(@PathVariable UUID id) {
        log.info("REST request to get terrain: {}", id);
        TerrainDTO terrain = terrainService.getTerrainById(id);
        return ResponseEntity.ok(terrain);
    }

    @GetMapping
    public ResponseEntity<List<TerrainDTO>> getAllTerrains(
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        log.info("REST request to get all terrains (activeOnly: {})", activeOnly);
        List<TerrainDTO> terrains = activeOnly
                ? terrainService.getAllActiveTerrains()
                : terrainService.getAllTerrains();
        return ResponseEntity.ok(terrains);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TerrainDTO> updateTerrain(
            @PathVariable UUID id,
            @Valid @RequestBody TerrainDTO terrainDTO) {
        log.info("REST request to update terrain: {}", id);
        TerrainDTO updatedTerrain = terrainService.updateTerrain(id, terrainDTO);
        return ResponseEntity.ok(updatedTerrain);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerrain(@PathVariable UUID id) {
        log.info("REST request to delete terrain: {}", id);
        terrainService.deleteTerrain(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteTerrain(@PathVariable UUID id) {
        log.info("REST request to hard delete terrain: {}", id);
        terrainService.hardDeleteTerrain(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/access/{accesType}")
    public ResponseEntity<List<TerrainDTO>> getTerrainsByAccesType(@PathVariable AccesType accesType) {
        log.info("REST request to get terrains by access type: {}", accesType);
        List<TerrainDTO> terrains = terrainService.getTerrainsByAccesType(accesType);
        return ResponseEntity.ok(terrains);
    }

    @GetMapping("/filter/lighting")
    public ResponseEntity<List<TerrainDTO>> getTerrainsWithLighting() {
        log.info("REST request to get terrains with lighting");
        List<TerrainDTO> terrains = terrainService.getTerrainsWithLighting();
        return ResponseEntity.ok(terrains);
    }

    @GetMapping("/filter/parking")
    public ResponseEntity<List<TerrainDTO>> getTerrainsWithParking() {
        log.info("REST request to get terrains with parking");
        List<TerrainDTO> terrains = terrainService.getTerrainsWithParking();
        return ResponseEntity.ok(terrains);
    }


    @GetMapping("/filter/changing-rooms")
    public ResponseEntity<List<TerrainDTO>> getTerrainsWithChangingRooms() {
        log.info("REST request to get terrains with changing rooms");
        List<TerrainDTO> terrains = terrainService.getTerrainsWithChangingRooms();
        return ResponseEntity.ok(terrains);
    }


    @GetMapping("/filter/rating")
    public ResponseEntity<List<TerrainDTO>> getTerrainsByMinimumRating(
            @RequestParam(defaultValue = "0.0") Double min) {
        log.info("REST request to get terrains with minimum rating: {}", min);
        List<TerrainDTO> terrains = terrainService.getTerrainsByMinimumRating(min);
        return ResponseEntity.ok(terrains);
    }

    @GetMapping("/filter/players")
    public ResponseEntity<List<TerrainDTO>> getTerrainsByPlayerRange(
            @RequestParam Integer min,
            @RequestParam Integer max) {
        log.info("REST request to get terrains for {} to {} players", min, max);
        List<TerrainDTO> terrains = terrainService.getTerrainsByPlayerRange(min, max);
        return ResponseEntity.ok(terrains);
    }


    @GetMapping("/search")
    public ResponseEntity<List<TerrainDTO>> searchTerrains(@RequestParam String q) {
        log.info("REST request to search terrains with keyword: {}", q);
        List<TerrainDTO> terrains = terrainService.searchTerrains(q);
        return ResponseEntity.ok(terrains);
    }
}