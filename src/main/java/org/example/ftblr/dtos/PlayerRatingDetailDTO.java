package org.example.ftblr.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerRatingDetailDTO {
    private UUID playerId;
    private String playerName;
    private String playerPosition;
    private String profilePicture;
    private double averageRating;
    private int ratingCount;
}
