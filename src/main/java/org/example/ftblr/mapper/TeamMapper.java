package org.example.ftblr.mapper;

import org.example.ftblr.dtos.TeamDTO;
import org.example.ftblr.Entity.Team;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {

    Team toEntity(TeamDTO dto);

    TeamDTO toDTO(Team entity);

    List<TeamDTO> toDTOList(List<Team> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TeamDTO dto, @MappingTarget Team entity);
}