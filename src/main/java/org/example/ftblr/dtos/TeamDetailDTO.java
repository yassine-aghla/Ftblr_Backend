package org.example.ftblr.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TeamDetailDTO {
    private UUID id;
    private String name;
    private String logo;
    private List<PlayerDetailDTO> players;
}
