package org.example.ftblr.Services;

import org.example.ftblr.dtos.auth.JwtResponse;
import org.example.ftblr.dtos.auth.LoginRequest;
import org.example.ftblr.dtos.auth.RegisterRequest;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    JwtResponse registerUser(RegisterRequest registerRequest);
}