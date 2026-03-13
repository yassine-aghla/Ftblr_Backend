package org.example.ftblr.mapper;

import org.example.ftblr.dtos.MatchDTO;
import org.example.ftblr.Entity.Match;
import org.example.ftblr.Entity.MatchParticipation;
import org.example.ftblr.dtos.MatchParticipationDTO;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @Mapping(target = "participations", ignore = true) // IGNORER les participations ici
    MatchDTO toDTO(Match entity);

    @Mapping(target = "terrain", ignore = true)
    @Mapping(target = "team1", ignore = true)
    @Mapping(target = "team2", ignore = true)
    @Mapping(target = "winnerTeam", ignore = true)
    @Mapping(target = "participations", ignore = true)
    Match toEntity(MatchDTO dto);

    List<MatchDTO> toDTOList(List<Match> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "terrain", ignore = true)
    @Mapping(target = "team1", ignore = true)
    @Mapping(target = "team2", ignore = true)
    @Mapping(target = "winnerTeam", ignore = true)
    @Mapping(target = "participations", ignore = true)
    void updateEntityFromDTO(MatchDTO dto, @MappingTarget Match entity);

    default List<MatchParticipationDTO> mapParticipations(List<MatchParticipation> participations) {
        if (participations == null) return null;
        return participations.stream()
                .map(this::toParticipationDTO)
                .collect(Collectors.toList());
    }

    @Mapping(target = "match", ignore = true)
    @Mapping(target = "matchId", source = "match.id")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "team", source = "team")
    MatchParticipationDTO toParticipationDTO(MatchParticipation participation);
}