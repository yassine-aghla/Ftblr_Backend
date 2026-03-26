package org.example.ftblr.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class GoalDetailDTO {
    private UUID scorerId;
    private String scorerName;
    private UUID assistId;
    private String assistName;
    private int minute;
    private String teamType;
}
