package org.example.ftblr.dtos.auth;

import lombok.Data;
import java.util.UUID;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public JwtResponse(String token, UUID id, String email, String firstName, String lastName, String role) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}