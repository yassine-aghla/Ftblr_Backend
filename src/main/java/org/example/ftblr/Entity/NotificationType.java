package org.example.ftblr.Entity;

public enum NotificationType {
    MATCH_RATING,        // Pour noter les joueurs après un match
    MATCH_RESULT,        // Résultat du match
    PLAYER_ABSENT,       // Joueur absent
    PLAYER_BANNED,       // Joueur banni
    MATCH_COMPLETED,     // Match terminé
    ORGANIZER_REQUEST,
    ORGANIZER_REQUEST_REJECTED,
    ORGANIZER_REQUEST_APPROVED
}