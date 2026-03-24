package org.example.ftblr.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    @Column(nullable = false)
    private Integer team1Score;

    @Column(nullable = false)
    private Integer team2Score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "man_of_match_id")
    private User manOfMatch;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isVerified;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isVerified == null) {
            isVerified = false;
        }
    }
}