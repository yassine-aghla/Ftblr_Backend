package org.example.ftblr.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "terrain")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Terrain {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String filedType;

    @Column(nullable = false)
    private String dimension;

    @Column(nullable = false)
    private Integer recommendedPlayers;

    @Column(nullable = false)
    private Boolean hasLighting;

    @Column(nullable = false)
    private Boolean hasParking;

    @Column(nullable = false)
    private Boolean hasChangingRooms;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccesType accesType;

    @ElementCollection
    @CollectionTable(name = "terrain_photos", joinColumns = @JoinColumn(name = "terrain_id"))
    @Column(name = "photo_url")
    private List<String> photos;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Boolean isActive;


    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (rating == null) {
            rating = 0.0;
        }
    }
}