package org.example.ftblr.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PlayerRatingDTO {
    private UUID id;

    @NotNull(message = "Rated player ID is required")
    private UUID ratedPlayerId;

    private String ratedPlayerName;

    @NotNull(message = "Match ID is required")
    private UUID matchId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    private String comment;
}