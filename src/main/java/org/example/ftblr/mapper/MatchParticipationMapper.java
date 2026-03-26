package org.example.ftblr.mapper;

import org.example.ftblr.Entity.MatchParticipation;
import org.example.ftblr.dtos.MatchParticipationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, TeamMapper.class})
public interface MatchParticipationMapper {

    @Mapping(source = "match.id", target = "matchId")
    @Mapping(target = "match", ignore = true)
    @Mapping(source = "user", target = "user")
    @Mapping(source = "team", target = "team")
    MatchParticipationDTO toDTO(MatchParticipation participation);

    List<MatchParticipationDTO> toDTOList(List<MatchParticipation> participations);

    @Mapping(source = "matchId", target = "match.id")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "teamId", target = "team.id")
    @Mapping(target = "match", ignore = true)
    MatchParticipation toEntity(MatchParticipationDTO participationDTO);

    void updateEntityFromDTO(MatchParticipationDTO dto, @MappingTarget MatchParticipation entity);
}