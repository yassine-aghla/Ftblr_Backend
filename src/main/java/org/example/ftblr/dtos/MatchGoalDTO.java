package org.example.ftblr.dtos;

import lombok.Data;
import org.example.ftblr.Entity.TeamType;

import java.util.UUID;

@Data
public class MatchGoalDTO {
    private UUID scorerId;
    private UUID assistId;
    private Integer minute;
    private TeamType teamType;
}