package org.example.ftblr.dtos;

import lombok.Data;
import org.example.ftblr.Entity.AttendanceStatus;

import java.util.UUID;

@Data
public class AttendanceDTO {
    private UUID playerId;
    private AttendanceStatus status;
}