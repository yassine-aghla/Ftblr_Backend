package org.example.ftblr.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.example.ftblr.Entity.TeamType;

import java.util.UUID;

@Data
public class MatchGoalDTO {
    private UUID scorerId;
    private UUID assistId;
    @Min(value = 1, message = "La minute doit être >= 1")
    @Max(value = 90, message = "La minute doit être <= 90")
    private Integer minute;
    private TeamType teamType;
}