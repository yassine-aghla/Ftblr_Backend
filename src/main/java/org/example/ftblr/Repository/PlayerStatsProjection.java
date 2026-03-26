package org.example.ftblr.Repository;

import java.util.UUID;

public interface PlayerStatsProjection {
    UUID getId();
    String getFirstName();
    String getLastName();
    String getProfilePicture();
    String getPosition();
    int getGoals();
    int getAssists();
    int getMatchesPlayed();
    double getAvgRating();
    double getAttendanceRate();
}