package org.example.ftblr.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class PlayerOfMonthDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String profilePicture;
    private int goals;
    private int assists;
    private int matchesPlayed;
    private double attendanceRate;
    private double rating;
    private String position;
    private String title;
}