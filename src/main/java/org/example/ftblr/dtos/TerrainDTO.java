package org.example.ftblr.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.ftblr.Entity.AccesType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerrainDTO {

    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Latitude is required")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "price is required")
    private BigDecimal price;

    @NotNull(message = "Longitude is required")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotBlank(message = "Filed type is required")
    private String filedType;

    @NotBlank(message = "Dimension is required")
    private String dimension;

    @NotNull(message = "Recommended players is required")
    @Min(value = 1, message = "Recommended players must be at least 1")
    private Integer recommendedPlayers;

    @NotNull(message = "Has lighting information is required")
    private Boolean hasLighting;

    @NotNull(message = "Has parking information is required")
    private Boolean hasParking;

    @NotNull(message = "Has changing rooms information is required")
    private Boolean hasChangingRooms;

    @NotNull(message = "Access type is required")
    private AccesType accesType;

    private List<String> photos;

    @Min(value = 0, message = "Rating must be between 0 and 5")
    @Max(value = 5, message = "Rating must be between 0 and 5")
    private Double rating;

    private Boolean isActive;
}