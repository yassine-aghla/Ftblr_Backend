package org.example.ftblr.mapper;

import org.example.ftblr.dtos.TerrainDTO;
import org.example.ftblr.Entity.Terrain;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TerrainMapper {

    TerrainDTO toDTO(Terrain terrain);

    @Mapping(target = "price", source = "price")
    Terrain toEntity(TerrainDTO terrainDTO);



    List<TerrainDTO> toDTOList(List<Terrain> terrains);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TerrainDTO terrainDTO, @MappingTarget Terrain terrain);
}