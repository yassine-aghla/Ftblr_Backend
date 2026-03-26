package org.example.ftblr.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerDetailDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String position;
    private String profilePicture;
    private double rating;
}
