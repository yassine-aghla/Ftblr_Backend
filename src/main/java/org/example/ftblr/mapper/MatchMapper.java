package org.example.ftblr.mapper;

import org.example.ftblr.dtos.MatchDTO;
import org.example.ftblr.Entity.Match;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TerrainMapper.class, TeamMapper.class})
public interface MatchMapper {

    @Mapping(target = "terrainId", source = "terrain.id")
    @Mapping(target = "terrain", source = "terrain")
    @Mapping(target = "team1Id", source = "team1.id")
    @Mapping(target = "team1", source = "team1")
    @Mapping(target = "team2Id", source = "team2.id")
    @Mapping(target = "team2", source = "team2")
    @Mapping(target = "winnerTeamId", source = "winnerTeam.id")
    @Mapping(target = "winnerTeam", source = "winnerTeam")
    @Mapping(target = "isFull", expression = "java(entity.isFull())")
    @Mapping(target = "remainingSlots", expression = "java(entity.getRemainingSlots())")
    @Mapping(target = "canJoin", expression = "java(entity.canJoin())")
    MatchDTO toDTO(Match entity);

    @Mapping(target = "terrain", ignore = true)
    @Mapping(target = "team1", ignore = true)
    @Mapping(target = "team2", ignore = true)
    @Mapping(target = "winnerTeam", ignore = true)
    Match toEntity(MatchDTO dto);

    List<MatchDTO> toDTOList(List<Match> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "terrain", ignore = true)
    @Mapping(target = "team1", ignore = true)
    @Mapping(target = "team2", ignore = true)
    @Mapping(target = "winnerTeam", ignore = true)
    void updateEntityFromDTO(MatchDTO dto, @MappingTarget Match entity);
}