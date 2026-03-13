package org.example.ftblr.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "matches", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_match_teams_time",
                columnNames = {"team1_id", "team2_id", "time"}
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchType matchType;

    @Column(nullable = false)
    private Integer playersNeeded;

    @Column(nullable = false)
    private Integer currentPlayers;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequiredLevel requiredLevel;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cout;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusMatch status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terrain_id", nullable = false)
    private Terrain terrain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team1_id", nullable = false)
    private Team team1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team2_id", nullable = false)
    private Team team2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    @Column
    private Integer scoreTeam1;

    @Column
    private Integer scoreTeam2;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MatchParticipation> participations = new ArrayList<>();

    public boolean isUserParticipating(UUID userId) {
        return participations.stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));
    }

    public MatchParticipation getUserParticipation(UUID userId) {
        return participations.stream()
                .filter(p -> p.getUser().getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public long getTeamPlayersCount(UUID teamId) {
        return participations.stream()
                .filter(p -> p.getTeam().getId().equals(teamId))
                .count();
    }

    public List<MatchParticipation> getTeamParticipations(UUID teamId) {
        return participations.stream()
                .filter(p -> p.getTeam().getId().equals(teamId))
                .collect(Collectors.toList());
    }

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currentPlayers == null) {
            currentPlayers = 0;
        }
        if (status == null) {
            status = StatusMatch.SCHEDULED;
        }
        validateTeams();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    private void validateTeams() {
        if (team1 != null && team1.equals(team2)) {
            throw new IllegalStateException("Team1 and Team2 must be different");
        }
    }

    public boolean isFull() {
        return currentPlayers >= playersNeeded;
    }

    public int getRemainingSlots() {
        return playersNeeded - currentPlayers;
    }

    public boolean canJoin() {
        return !isFull() && status == StatusMatch.SCHEDULED;
    }

    public Team getOpponent(Team team) {
        if (team.equals(team1)) return team2;
        if (team.equals(team2)) return team1;
        throw new IllegalArgumentException("Team not part of this match");
    }

    public boolean isTeamParticipating(UUID teamId) {
        return (team1 != null && team1.getId().equals(teamId)) ||
                (team2 != null && team2.getId().equals(teamId));
    }
}